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

import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.kitodo.mediaserver.core.db.entities.Collection;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * JPA Specification to build search querys for Work items.
 */
public class WorkJpaSpecification implements Specification<Work> {

    private List<Map.Entry<String, String>> criterias;

    public List<Map.Entry<String, String>> getCriterias() {
        return criterias;
    }

    /**
     * Constructor with given search criterias.
     * @param criterias search criterias as key:value list
     */
    public WorkJpaSpecification(List<Map.Entry<String, String>> criterias) {
        super();
        this.criterias = criterias;
    }

    @Override
    public Predicate toPredicate(Root<Work> works, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        Predicate predicate = criteriaBuilder.conjunction();
        Join<Work, Collection> collections = works.join("collections", JoinType.LEFT);
        query.distinct(true);

        for (final Map.Entry<String, String> criteria : criterias) {

            if (StringUtils.hasText(criteria.getKey())) {

                // key given: search in specific field named like key
                switch (criteria.getKey()) {
                    case "collection":
                        predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(collections.get("name"), "%" + criteria.getValue() + "%")
                        );
                        break;
                    case "hostId":
                        Predicate paramPredicate = criteriaBuilder.disjunction();
                        // search works with hostId==workId
                        paramPredicate = criteriaBuilder.or(paramPredicate,
                            criteriaBuilder.like(works.get("id"), "%" + criteria.getValue() + "%")
                        );
                        // search for hostId (same as default)
                        paramPredicate = criteriaBuilder.or(paramPredicate,
                            criteriaBuilder.like(works.get(criteria.getKey()), "%" + criteria.getValue() + "%")
                        );
                        predicate = criteriaBuilder.and(predicate, paramPredicate);
                        break;
                    default:
                        predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(works.get(criteria.getKey()), "%" + criteria.getValue() + "%")
                        );
                        break;
                }

            } else {
                // no key given: search for value in all fields
                Predicate paramPredicate = criteriaBuilder.disjunction();
                paramPredicate = criteriaBuilder.or(paramPredicate,
                    criteriaBuilder.like(works.get("id"), "%" + criteria.getValue() + "%")
                );
                paramPredicate = criteriaBuilder.or(paramPredicate,
                    criteriaBuilder.like(works.get("title"), "%" + criteria.getValue() + "%")
                );
                paramPredicate = criteriaBuilder.or(paramPredicate,
                    criteriaBuilder.like(works.get("hostId"), "%" + criteria.getValue() + "%")
                );
                paramPredicate = criteriaBuilder.or(paramPredicate,
                    criteriaBuilder.like(collections.get("name"), "%" + criteria.getValue() + "%")
                );
                predicate = criteriaBuilder.and(predicate, paramPredicate);
            }
        }

        return predicate;
    }
}
