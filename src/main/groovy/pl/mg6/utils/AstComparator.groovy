package pl.mg6.utils

import org.codehaus.groovy.ast.ASTNode

class AstComparator {

    static AstAssert assertThat(List<ASTNode> actual) {
        return new AstAssert(actual: actual)
    }

    static class AstAssert {

        List<ASTNode> actual

        void isEqualTo(List<ASTNode> expected) {

        }
    }
}
