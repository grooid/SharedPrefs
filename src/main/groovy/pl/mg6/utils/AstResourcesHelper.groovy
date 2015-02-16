package pl.mg6.utils

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.builder.AstBuilder

class AstResourcesHelper {

    private static final String actualExtension = ".actual"
    private static final String expectedExtension = ".expected"

    List<String> getAllTests(String directory) {
        def root = new File(getClass().getResource(directory).toURI())
        String[] actualFiles = filesWithExtension(root, actualExtension)
        String[] expectedFiles = filesWithExtension(root, expectedExtension)
        throwIfMissing(actualFiles, expectedFiles, expectedExtension)
        throwIfMissing(expectedFiles, actualFiles, actualExtension)
        return actualFiles.collect { String name -> name.substring(0, name.lastIndexOf('.')) }
    }

    List<List<ASTNode>> loadTest(String test) {
        return [
                loadTestImpl(test + actualExtension),
                loadTestImpl(test + expectedExtension),
        ]
    }

    private loadTestImpl(String test) {
        new AstBuilder().buildFromString(getClass().getResource(test).text)
    }

    private static String[] filesWithExtension(File root, String extension) {
        return root.list { File dir, String name -> name.endsWith(extension) }
    }

    private static void throwIfMissing(String[] lookupFiles, String[] checkedFiles, String checkedExtension) {
        lookupFiles.each { String name ->
            def maybeMissingName = name.substring(0, name.lastIndexOf('.')) + checkedExtension
            if (!checkedFiles.contains(maybeMissingName)) {
                throw new RuntimeException("$maybeMissingName is missing")
            }
        }
    }
}
