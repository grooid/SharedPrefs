package pl.mg6.utils

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.stmt.BlockStatement

class AstComparator {

    static AstAssert assertThat(List<ASTNode> actual) {
        return new AstAssert(actual: actual)
    }

    static class AstAssert {

        List<ASTNode> actual

        void isEqualTo(List<ASTNode> expected) {
            assertSyntaxTree(expected, actual)
        }
    }

    private static void assertSyntaxTree(List<? extends ASTNode> expected, List<? extends ASTNode> actual) {
        if (expected.size() != actual.size()) {
            throw new RuntimeException("Node count different. \nActual: ${actual.size()} \nExpect: ${expected.size()} ")
        }
        [expected, actual].transpose().each { ASTNode eNode, ASTNode aNode ->
            println eNode.class.simpleName
            println aNode.class.simpleName
            if (eNode.class != aNode.class) {
                throw new RuntimeException("Different types of nodes. \nActual: ${aNode.class.simpleName} \nExpected: ${eNode.class.simpleName} ")
            }
            def name = eNode.class.simpleName
            if (nodeToActionMap.containsKey(name)) {
                def action = nodeToActionMap.get(name)
                action(eNode, aNode)
            } else {
                throw new RuntimeException("Missing definition for $name. Update nodeToActionMap")
            }
        }
    }

    private static Map<String, Closure> nodeToActionMap = [
            BlockStatement: { BlockStatement expected, BlockStatement actual ->
                assertSyntaxTree(expected.statements, actual.statements)
            },
            ClassNode     : { ClassNode expected, ClassNode actual ->
                if (expected.name != actual.name) {
                    throw new RuntimeException("Different class names. \nActual: $actual.name \nExpect: $expected.name ")
                }
            }
    ]
}
