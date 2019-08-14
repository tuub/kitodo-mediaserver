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

package org.kitodo.mediaserver.core.processors;

import java.nio.file.Path;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

@RunWith(SpringRunner.class)
public class XsltMetsTocReaderTest {

    private XsltMetsTocReader tocReader = new XsltMetsTocReader();
    private Path testMetsFile;

    @Before
    public void init() throws Exception {
        tocReader.setXslt(new ClassPathResource("xslt/tocFromMets.xsl"));
        testMetsFile = ResourceUtils.getFile("classpath:mets/BV037808438.xml").toPath();
    }

    @Test
    public void getToc() throws Exception {
        //given

        //when
        Toc toc = tocReader.read(testMetsFile);

        //then
        assertThat(toc,
            hasProperty("tocItems",
                contains(
                    allOf(
                        hasProperty("name", is("Excellentissimo , illustrissimo Reverendissimo Domino, Adamo E.L. B. Fatachich de Zajezda")),
                        hasProperty("type", is("dedication")),
                        hasProperty("pageNumber", is(5)),
                        hasProperty("children", nullValue())
                    ),
                    allOf(
                        hasProperty("name", is("Praefatio")),
                        hasProperty("type", is("preface")),
                        hasProperty("pageNumber", is(7)),
                        hasProperty("children", nullValue())
                    ),
                    allOf(
                        hasProperty("name", is("Index rerum")),
                        hasProperty("type", is("contents")),
                        hasProperty("pageNumber", is(11)),
                        hasProperty("children", nullValue())
                    ),
                    allOf(
                        hasProperty("name", is("Sectio prima de Materiarum conditione, et aedi ficandi legibus")),
                        hasProperty("type", is("chapter")),
                        hasProperty("pageNumber", is(21)),
                        hasProperty("children",
                            contains(
                                allOf(
                                    hasProperty("name", is("Caput primum de Lignis")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(21)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput secundum de lapidibus")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(27)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput tertium")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(30)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput quartum")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(36)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput quintum")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(39)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput sextum")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(46)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput septimum")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(54)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput octavum")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(68)),
                                    hasProperty("children", nullValue())
                                )
                            )
                        )
                    ),
                    allOf(
                        hasProperty("name", is("Sectio Secunda de structuris oeconomics")),
                        hasProperty("type", is("chapter")),
                        hasProperty("pageNumber", is(73)),
                        hasProperty("children",
                            contains(
                                allOf(
                                    hasProperty("name", is("Caput prinum de structurae ad necessitatem domesticam pertinentes")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(73)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput secundum sturcturae oeconomae domesticae et rurali communes")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(83)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput tertium aedifica oeconomiae majoris et ruralis")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(102)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput quartum de aede rustica")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(122)),
                                    hasProperty("children", nullValue())
                                )
                            )
                        )
                    ),
                    allOf(
                        hasProperty("name", is("Sectio tertia de sumtuum calculo")),
                        hasProperty("type", is("chapter")),
                        hasProperty("pageNumber", is(137)),
                        hasProperty("children",
                            contains(
                                allOf(
                                    hasProperty("name", is("Caput prinum")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(137)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput secundum")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(143)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput tertium de dimensione partium aedifici")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(151)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput quartum de determinanda quantitate materiarum et sumtuum")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(159)),
                                    hasProperty("children", nullValue())
                                ),
                                allOf(
                                    hasProperty("name", is("Caput quintum constitutione pretii materiarum et mercedis")),
                                    hasProperty("type", is("chapter")),
                                    hasProperty("pageNumber", is(168)),
                                    hasProperty("children", nullValue())
                                )
                            )
                        )
                    ),
                    allOf(
                        hasProperty("name", is("Taf I-X")),
                        hasProperty("type", is("figure")),
                        hasProperty("pageNumber", is(181)),
                        hasProperty("children", nullValue())
                    )
                )
            )
        );

    }

}
