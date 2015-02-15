package pl.mg6.utils

import spock.lang.Specification

class StringUtilsSpec extends Specification {

    def "should make everything lowercase with underscores"() {
        expect:
        StringUtils.toLowercaseWithUnderscores(input) == output
        where:
        input                     | output
        'lowercase'               | 'lowercase'
        'camelCase'               | 'camel_case'
        'PascalCase'              | 'pascal_case'
        'UPPERCASE'               | 'uppercase'
        'endsWithUPPERCASE'       | 'ends_with_uppercase'
        'uppercaseINTheMiddle'    | 'uppercase_in_the_middle'
        'STARTSWithUppercase'     | 'starts_with_uppercase'
        'UPPERCASE_UNDERSCORES'   | 'uppercase_underscores'
        'Capitalized_Underscores' | 'capitalized_underscores'
    }
}
