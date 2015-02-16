package pl.mg6.utils

import spock.lang.Specification

import static pl.mg6.utils.AstComparator.assertThat

class AstComparatorSpec extends Specification {

    def "should fail on different class names"() {
        def test = new AstResourcesHelper().loadTest("/astcomparator/DifferentClassNames")
        when:
        assertThat test[0] isEqualTo test[1]
        then:
        def e = thrown(RuntimeException)
        e.message == "Different class names. \nActual: ActualClassName \nExpect: ExpectedClassName "
    }
}