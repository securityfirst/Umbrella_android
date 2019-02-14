package org.secfirst.umbrella.whitelabel.misc

const val LESSON_HOST = "lesson"
const val FORM_HOST = "forms"
const val FEED_HOST = "feed"
const val CHECKLIST_HOST = "checklist"
/*
 Scheme to define how many levels a URI should have.
 Eg: umbrella://lesson/glossary{1}
 Open a specific segment for category (glossary)
 */
const val LESSON_MODULE_LEVEL = 1
/*
 Scheme to define how many levels a URI should have.
 Eg: umbrella://lesson/information{1}/beginner{2}/email{3}
 Open a specific difficulty for subject (email)
 */
const val LESSON_SUBJECT_LEVEL = 3

/*
 Scheme to define how many levels a URI should have.
 Eg: umbrella://lesson/glossary{1}/android-apk{2}
 Open a specific segment for category (android-apk)
 umbrella://lesson/tools/tor
 */
const val LESSON_SEGMENT_IN_MODULE = 2

/*
 Scheme to define how many levels a URI should have.
 Eg: umbrella://lesson/information{1}/beginner{2}/email{3}/what_now{4}
 Open a specific segment for subject (email)
 */
const val LESSON_SEGMENT_IN_SUBJECT = 4