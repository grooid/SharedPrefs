package pl.mg6.utils

import spock.lang.Specification

class AstResourceHelperSpec extends Specification {

    def helper = new AstResourcesHelper()

    def "should return empty list"() {
        when:
        def allTests = helper.getAllTests("/astresourcehelper/zero/")
        then:
        allTests == []
    }

    def "should return non-empty list"() {
        when:
        def allTests = helper.getAllTests("/astresourcehelper/happy/")
        then:
        allTests == ["First", "Second"]
    }

    def "should throw exception on missing expected"() {
        when:
        helper.getAllTests("/astresourcehelper/expected_missing")
        then:
        def e = thrown(RuntimeException)
        e.message == "First.expected is missing"
    }

    def "should throw exception on missing actual"() {
        when:
        helper.getAllTests("/astresourcehelper/actual_missing")
        then:
        def e = thrown(RuntimeException)
        e.message == "Second.actual is missing"
    }
}
