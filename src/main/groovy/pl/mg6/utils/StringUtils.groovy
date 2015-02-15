package pl.mg6.utils

class StringUtils {

    static String toLowercaseWithUnderscores(String value) {
        def magic = /(?<![_A-Z]|^)[A-Z]|(?<=[A-Z])[A-Z](?![_A-Z]|$)/
        return value.replaceAll(magic, '_$0').toLowerCase()
    }
}
