package models


object StringConverter {
  implicit def StringSanatizeConvert(input: String) : NameSanitizer = {
    new NameSanitizer(input)
  }

  class NameSanitizer(input : String) {
    def sanitize : String = {
      input.replaceAll("/", "_")
    }
  }
}
