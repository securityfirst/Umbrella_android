package org.secfirst.umbrella.models;

import java.util.ArrayList;

public class InitialData {

    private static ArrayList<Segment> segmentList = new ArrayList<Segment>();
    private static ArrayList<CheckItem> checkList = new ArrayList<CheckItem>();
    private static ArrayList<Category> categoryList = new ArrayList<Category>();
    public static final int PASSWORDS = 1;
    public static final int MOBILE_PHONES = 2;
    public static final int STAYING_ANONYMOUS_ONLINE = 3;
    public static final int SAFE_DELETING = 4;

    public static ArrayList<Segment> getSegmentList() {
        segmentList.add(new Segment("Mobile Phones", "", "Mobile phones (including this one in your hand), have a number of dangers. Without the use of special apps, it is easy for people to listen to your calls and view the messages (both SMS and Instant Messages like Whatsapp). Phones can also be used to listen in on what is happening in your room, as they can be turned on remotely – even when the phone is turned off. To prevent you being tracked or listened into during sensitive activities, it is advised that you remove the battery from your phone or leave it away from the area where you are working in.", MOBILE_PHONES));

        segmentList.add(new Segment("The best situation is to separate your activities, so that your most sensitive work is done on a cheap phone – which you can dispose of if necessary. Only turn this on when you need to, delete the call, SMS and other logs immediately and never call or contact your main phone on it - as you will then link it on phone company databases.", MOBILE_PHONES));

        segmentList.add(new Segment("Also, it Is not enough just to change your SIM card regularly, you need to change your cheap phone also. This is because your SIM card and your phone have unique numbers which can identify you. Just changing one means you are still at risk. Also, it is important not to create call patterns by ringing the same people as previously  on a new phone – if you do that the phone company software will eventually match the calls and find out that you are the same person. For instance if you ring the same people (your son, husband, office, mother etc) you are making a unique pattern. Thus try to keep activities separate and keep changing your methods.", MOBILE_PHONES));


        segmentList.add(new Segment("Passwords", "", "Because remembering many different passwords is difficult, people often reuse a small number of passwords across many different accounts, sites, and services. Today, users are constantly being asked to come up with new passwords—many people end up re-using the same password dozens or even hundreds of times.", PASSWORDS));

        segmentList.add(new Segment("Reusing passwords is an exceptionally bad security practice, because if an attacker gets hold of one password, she will often try using that password on various accounts belonging to the same person. If that person has reused the same password several times, the attacker will be able to access multiple accounts. That means a given password may be only as secure as the least secure service where it's been used.", PASSWORDS));

        segmentList.add(new Segment("Avoiding password re-use is a valuable security precaution, but you won't be able to remember all your passwords if each one is different. Fortunately, there are software tools to help with this—a password manager (also called a password safe) is a software application that helps store a large number of passwords safely. This makes it practical to avoid using the same password in multiple contexts. The password manager protects all of your passwords with a single master password(or, ideally a passphrase—see discussion below) so you only have to remember one thing. People who use a password manager no longer actually know the passwords for their different accounts; the password manager can handle the entire process of creating and remembering the passwords for them.", PASSWORDS));

        segmentList.add(new Segment("Stay Anonymous Online", "", "Many governments, companies, schools, and public access points use software to prevent Internet users from accessing certain websites and Internet services.  This is called Internet filtering or blocking and is a form of censorship. Content filtering comes in different forms.  Sometimes entire websites are blocked, sometimes individual web pages, and sometimes content is blocked based on keywords contained in it. One country might block Facebook entirely, or only block particular Facebook group pages—or it might block any page or web search with the words “falun gong” in it. ", STAYING_ANONYMOUS_ONLINE));

        segmentList.add(new Segment("Regardless of how content is filtered or blocked, you can almost always get the information you need by using a circumvention tool.  Circumvention tools usually work by diverting your web or other traffic through another computer, so that it bypasses the machines conducting the censorship. An intermediary service through which you channel your communications in this process is called a proxy. ", STAYING_ANONYMOUS_ONLINE));

        segmentList.add(new Segment("Circumvention tools do not necessarily provide additional security or anonymity, even those that promise privacy or security, even ones that have terms like “anonymizer” in their names.", STAYING_ANONYMOUS_ONLINE));

        segmentList.add(new Segment("There are different ways of circumventing Internet censorship, some of which provide additional layers of security.  The tool that is most appropriate for you depends on your threat model.", STAYING_ANONYMOUS_ONLINE));

        segmentList.add(new Segment("Safe Deleting", "", "Most of us think that a file on our computer is deleted once we put the file in our computer's trash folder and empty the trash; in reality, deleting the file does not completely erase it.  When one does this, the computer just makes the file invisible to the user and marks the part of the disk that it is the file was stored as \"available”—meaning that your operating system can now write over the file with new data. Therefore, it may be weeks, months, or even years before that file is overwritten with a new one.  Until this happens, that “deleted” file is still on your disk; it’s just invisible to normal operations.  And with a little work and the right tools (such as “undelete” software or forensic methods), you can even still retrieve the “deleted” file. The bottom line is that computers normally don't \"delete\" files; they just allow the space those files take up to be overwritten by something else some time in the future.", SAFE_DELETING));

        segmentList.add(new Segment("The best way to delete a file forever, then, is to make sure it gets overwritten immediately, in a way that makes it difficult to retrieve what used to be written there. Your operating system probably already has software that can do this for you—software that can overwrite all of the \"empty\" space on your disk with gibberish and thereby protect the confidentiality of deleted data. Examples of this type of software include GNU shred (Linux) and Secure Delete (Mac OS X).  On Windows, we will describe how to use a third-party software tool called Eraser.", SAFE_DELETING));

        return segmentList;
    }

    public static ArrayList<CheckItem> getCheckList() {
        checkList.add(new CheckItem("Buy a separate, pay as you go SIM card and phone", MOBILE_PHONES));

        checkList.add(new CheckItem("Only use this for sensitive communication – never for anything else", MOBILE_PHONES));

        checkList.add(new CheckItem("Ensure it has a strong PIN password on it and change it frequently", MOBILE_PHONES));

        checkList.add(new CheckItem("Do not install any applications on it", MOBILE_PHONES));

        checkList.add(new CheckItem("Try not to turn it on and use it when your personal phone is also on", MOBILE_PHONES));

        checkList.add(new CheckItem("Do not turn off your normal phone then immediately turn on your sensitive phone, it will create a pattern", MOBILE_PHONES));

        checkList.add(new CheckItem("Avoid storing numbers or names in in the phone",  MOBILE_PHONES));

            checkList.add(new CheckItem("Avoid storing numbers or names in in the phone", "Only those which are essential", false, 1,  MOBILE_PHONES));

            checkList.add(new CheckItem("Avoid storing numbers or names in in the phone", "Delete all call records and old SMS immediately", false, 1,  MOBILE_PHONES));

        checkList.add(new CheckItem("If possible have more than one phone for sensitive communication", MOBILE_PHONES));

        checkList.add(new CheckItem("Ideally get someone to text you a prearranged code from their phone, turn off your sensitive phone and ring them from a random payphone without CCTV.", MOBILE_PHONES));

        checkList.add(new CheckItem("Ideally get someone to text you a prearranged code from their phone, turn off your sensitive phone and ring them from a random payphone without CCTV.", "Remember, you might be secure but the person on the other end of the phone might not be", false, 1, MOBILE_PHONES));

        checkList.add(new CheckItem("Always use strong passwords", PASSWORDS));

        checkList.add(new CheckItem("Never reuse passwords", PASSWORDS));

        checkList.add(new CheckItem("Regularly change your passwords", PASSWORDS));

        checkList.add(new CheckItem("Search online to see has your password been exposed", PASSWORDS));

        checkList.add(new CheckItem("Use a password storage tool like KeePass", PASSWORDS));

        checkList.add(new CheckItem("Delete emails already read unless it is absolutely necessary to keep them.", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Use a number of different email addresses", STAYING_ANONYMOUS_ONLINE));

            checkList.add(new CheckItem("Use a number of different email addresses", "Use them as separate channels for different things", false, 1, STAYING_ANONYMOUS_ONLINE));
            checkList.add(new CheckItem("Use a number of different email addresses", "Never cross-contaminate them", false, 1, STAYING_ANONYMOUS_ONLINE));
            checkList.add(new CheckItem("Use a number of different email addresses", "Dispose and change them regularly", false, 1, STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Regularly delete saved messages", STAYING_ANONYMOUS_ONLINE));

            checkList.add(new CheckItem("Regularly delete saved messages", "Do not save messages in the “Sent Items folder”", false, 1, STAYING_ANONYMOUS_ONLINE));
            checkList.add(new CheckItem("Regularly delete saved messages", "Ensue messages are fully deleted from the trash.", false, 1, STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("As a last resort, some users may send catalogues or communications via TrueCrypt packages and other email systems", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Never, ever download and/or anything from someone you do not know and make sure all attachments are scanned", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("If your are unsure about an attachment sent by someone you do know, do not open it and contact them to see did they actually send it to you", STAYING_ANONYMOUS_ONLINE));
            checkList.add(new CheckItem("If your are unsure about an attachment sent by someone you do know, do not open it and contact them to see did they actually send it to you", "It is very easy to fake an email from someone you know", false, 1, STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Do not use your real details to signup for a service/email address etc.", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Never use paid services as the credit card can be linked back to you.", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("If using services such as Gmail or Yahoo for non-sensitive emails, regularly check the “Recent Account Activity” button at the bottom of the page to see if someone else has logged into your account ", STAYING_ANONYMOUS_ONLINE));
            checkList.add(new CheckItem("If using services such as Gmail or Yahoo for non-sensitive emails, regularly check the “Recent Account Activity” button at the bottom of the page to see if someone else has logged into your account ", "This checking is only for hackers who have low skills so should not be thought of as secure against intelligence agencies", false, 1, STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Social networking sites are very useful for intelligence agencies who wish to track you. Do not join groups which link you to causes which they may be interested in. Also, do not add anyone as a friend who may also be the subject of observation – as it is easy to map their network and discover you also.", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Always securely delete browsing histories using the “Tracks Eraser Pro” program you have been provided with.", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("If you are not using it, turn off your wireless modem (both physically and via the software options) as it potentially leaves an open door to your computer", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Always use and update your anti-virus software", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Change passwords as soon as an employee/intern/volunteer leaves the organisation", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Voice over Internet – Skype to Skype conversations are currently considered to be one of the most secure methods to speak. (However, governments such as the USA/UK/Russia/China/Israel can intercept Skype so this needs to be factored into consideration)", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Internet cafes should not be considered to be secure", STAYING_ANONYMOUS_ONLINE));
            checkList.add(new CheckItem("Internet cafes should not be considered to be secure", "As they can easily defeat all security measures by having “Trojan viruses” or key-loggers on their computers. This negates the need for the ability to break encryption by instead stealing the password directly.", false, 1, STAYING_ANONYMOUS_ONLINE));
            checkList.add(new CheckItem("Internet cafes should not be considered to be secure", "Always assume that the people in the internet café can see exactly what you can see on your screen – as usually they can", false, 1, STAYING_ANONYMOUS_ONLINE));
            checkList.add(new CheckItem("Internet cafes should not be considered to be secure", "Intelligence agencies are also very much aware of the dangers that internet cafes pose so they often monitor them in a number of ways – physically, electronically etc.", false, 1, STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Disable all Java, JavaScript and ActiveX and all add-ons (Except for PGP or TOR related add-ons)", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Do not use Microsoft Internet Explorer, instead use Secure Firefox", STAYING_ANONYMOUS_ONLINE));

        checkList.add(new CheckItem("Regularly delete files you are not using", SAFE_DELETING));

        checkList.add(new CheckItem("Make sure to delete your recycle bin or trash", SAFE_DELETING));

        checkList.add(new CheckItem("Use a safe deletion program", SAFE_DELETING));

        return checkList;
    }

    public static ArrayList<Category> getCategoryList() {
        categoryList.add(new Category(1, 6, "Passwords"));
        categoryList.add(new Category(2, 6, "Mobile Phones"));
        categoryList.add(new Category(3, 6, "Stay Anonymous Online"));
        categoryList.add(new Category(4, 6, "Safe deleting"));
        categoryList.add(new Category(5, 0, "My Security"));
        categoryList.add(new Category(6, 0, "Communications"));
        categoryList.add(new Category(7, 0, "Personal"));
        categoryList.add(new Category(8, 0, "Travel"));
        categoryList.add(new Category(9, 0, "Operations"));
        categoryList.add(new Category(10, 0, "Home / Office"));
        categoryList.add(new Category(11, 0, "Computer Network"));
        categoryList.add(new Category(12, 0, "Glossary"));
        categoryList.add(new Category(13, 0, "Index"));
        return categoryList;
    }

}
