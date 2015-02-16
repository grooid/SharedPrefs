package pl.mg6.grooid

import pl.mg6.utils.AstResourcesHelper

import static pl.mg6.utils.AstComparator.assertThat

class AllTransformationsTestCase extends GroovyTestCase {

    private static final String testRoot = "/test/"

    void testThemAll() {
        def helper = new AstResourcesHelper()
        def allTests = helper.getAllTests(testRoot)
        allTests.each {
            def test = helper.loadTest(testRoot + it)
            assertThat test[0] isEqualTo test[1]
        }
    }
}
