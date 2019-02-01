/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 */

package org.kitodo.mediaserver.core.db.specifications;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.kitodo.mediaserver.core.db.entities.Collection;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.processors.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * JPA Specification to build search querys for Work items.
 */
public class WorkJpaSpecification implements Specification<Work> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkJpaSpecification.class);

    private List<Map.Entry<String, Map.Entry<Operator, String>>> criterias;

    public List<Map.Entry<String, Map.Entry<Operator, String>>> getCriterias() {
        return criterias;
    }

    /**
     * Constructor with given search criterias.
     * @param criterias search criterias as key:value list
     */
    public WorkJpaSpecification(List<Map.Entry<String, Map.Entry<Operator, String>>> criterias) {
        super();
        this.criterias = criterias;
    }

    @Override
    public Predicate toPredicate(Root<Work> works, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        Predicate predicate = criteriaBuilder.conjunction();
        Join<Work, Collection> collections = works.join("collections", JoinType.LEFT);
        query.distinct(true);

        List<Map.Entry<String, Map.Entry<Operator, String>>> removedCriterias = new ArrayList<>();

        for (final Map.Entry<String, Map.Entry<Operator, String>> criteria : criterias) {

            Operator operator = criteria.getValue().getKey();
            Comparable value = criteria.getValue().getValue();

            if (StringUtils.hasText(criteria.getKey())) {

                // Choose the key
                Expression key;
                if ("collection".equals(criteria.getKey())) {
                    key = collections.get("name");
                } else {
                    key = works.get(criteria.getKey());
                }

                if ("indexTime".equals(criteria.getKey())) {
                    // This is a date field

                    if (operator == Operator.CONTAINS || operator == Operator.NOT_CONTAINS) {
                        // Date has to match exactly, ignore this expression
                        LOGGER.warn("Operator '" + operator + "' is not supported for Date type.");
                        removedCriterias.add(criteria);
                        continue;
                    }

                    // Parse Instant type from String
                    // Allow "now", a date and a date+time to be givven
                    LocalDateTime indexTime = null;
                    if ("now".equals(value)) {
                        indexTime = LocalDateTime.now();
                    } else if (!"".equals(value)) {

                        List<DateTimeFormatter> formatters = new ArrayList<>(Arrays.asList(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        ));

                        // Try to parse the string with these formatters
                        for (DateTimeFormatter formatter : formatters) {
                            try {
                                indexTime = LocalDateTime.parse((String) value, formatter);
                                break;
                            } catch (Exception ex) {
                                // ignore
                            }
                        }

                        if (indexTime == null) {
                            // Can not parse indexTime string to a valid date; ignore this expression
                            LOGGER.warn("Could not parse '" + value + "' as Date type.");
                            removedCriterias.add(criteria);
                            continue;
                        }
                    }

                    // Get Instant from local date
                    value = indexTime == null ? null : indexTime.atZone(ZoneId.systemDefault()).toInstant();
                }

                // Choose the expression by given operator
                Expression<Boolean> expression;
                Expression<Boolean> nullExpression = null;
                switch (criteria.getValue().getKey()) {
                    case LESS:
                        expression = criteriaBuilder.lessThan(key, value);
                        break;
                    case LESS_OR_EQUAL:
                        expression = criteriaBuilder.lessThanOrEqualTo(key, value);
                        if (value == null || "".equals(value)) {
                            nullExpression = criteriaBuilder.isNull(key);
                        }
                        break;
                    case GREATER:
                        expression = criteriaBuilder.greaterThan(key, value);
                        break;
                    case GREATER_OR_EQUAL:
                        expression = criteriaBuilder.greaterThanOrEqualTo(key, value);
                        if (value == null || "".equals(value)) {
                            nullExpression = criteriaBuilder.isNull(key);
                        }
                        break;
                    case EQUAL:
                        expression = criteriaBuilder.equal(key, value);
                        if (value == null || "".equals(value)) {
                            nullExpression = criteriaBuilder.isNull(key);
                        }
                        break;
                    case NOT_EQUAL:
                        expression = criteriaBuilder.notEqual(key, value);
                        if (!"".equals(value)) {
                            nullExpression = criteriaBuilder.isNull(key);
                        }
                        break;
                    case CONTAINS:
                        expression = criteriaBuilder.like(key, "%" + value + "%");
                        if ("".equals(value)) {
                            nullExpression = criteriaBuilder.isNull(key);
                        }
                        break;
                    case NOT_CONTAINS:
                        expression = criteriaBuilder.notLike(key, "%" + value + "%");
                        if (!"".equals(value)) {
                            nullExpression = criteriaBuilder.isNull(key);
                        }
                        break;
                    default:
                        // without an operator there is something wrong with this expression, ignore it
                        LOGGER.warn("No operator given for key '" + criteria.getKey() + "'.");
                        removedCriterias.add(criteria);
                        continue;
                }

                // If value was empty, check for empty field and NULL
                if (nullExpression != null) {
                    Predicate paramPredicate = criteriaBuilder.disjunction();
                    paramPredicate = criteriaBuilder.or(paramPredicate, nullExpression);
                    paramPredicate = criteriaBuilder.or(paramPredicate, expression);
                    predicate = criteriaBuilder.and(predicate, paramPredicate);
                } else {
                    predicate = criteriaBuilder.and(predicate, expression);
                }

            } else {
                // no key given: search for value in all fields
                Predicate paramPredicate = criteriaBuilder.disjunction();
                paramPredicate = criteriaBuilder.or(paramPredicate,
                    criteriaBuilder.like(works.get("id"), "%" + value + "%")
                );
                paramPredicate = criteriaBuilder.or(paramPredicate,
                    criteriaBuilder.like(works.get("title"), "%" + value + "%")
                );
                paramPredicate = criteriaBuilder.or(paramPredicate,
                    criteriaBuilder.like(works.get("hostId"), "%" + value + "%")
                );
                paramPredicate = criteriaBuilder.or(paramPredicate,
                    criteriaBuilder.like(collections.get("name"), "%" + value + "%")
                );
                predicate = criteriaBuilder.and(predicate, paramPredicate);
            }
        }

        criterias.removeAll(removedCriterias);

        return predicate;
    }
}
