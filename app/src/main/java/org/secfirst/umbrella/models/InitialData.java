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

        checkList.add(new CheckItem("Implement ‘need to know’ policy", "", false, 0, 3, 1));

        checkList.add(new CheckItem("Delete any unnecessary sensitive information", "", false, 0, 3, 1));

        checkList.add(new CheckItem("Make a list of information that you keep, where it’s kept, who has access to it, and what stops others from accessing it", "", false, 0, 3, 1));

        checkList.add(new CheckItem("Make a list of who might want to get a hold of your information", "", false, 0, 3, 1));

        checkList.add(new CheckItem("Note what your adversary might want to do with your information", "", false, 0, 3, 1));

        checkList.add(new CheckItem("Note the likelihood of your adversaries attacking", "", false, 0, 3, 1));

        checkList.add(new CheckItem("Note the capability of your adversaries to attack", "", false, 0, 3, 1));

        checkList.add(new CheckItem("Note what the consequences are if the attack succeeds", "", false, 0, 3, 1));

        checkList.add(new CheckItem("Note what you are you willing to go through in order to try to prevent them", "", false, 0, 3, 1));

        checkList.add(new CheckItem("Protect your computer", "", false, 0, 4, 1));

        checkList.add(new CheckItem("Install antivirus on all your devices", "", false, 10, 4, 1));

        checkList.add(new CheckItem("Install a malware scanner on all your devices", "", false, 10, 4, 1));

        checkList.add(new CheckItem("Look out for indicators of malware", "", false, 10, 4, 1));

        checkList.add(new CheckItem("Keep your software updated", "", false, 10, 4, 1));

        checkList.add(new CheckItem("Only download from official sites", "", false, 10, 4, 1));

        checkList.add(new CheckItem("Avoid opening suspicious attachments", "", false, 10, 4, 1));

        checkList.add(new CheckItem("Open unusual attachments in Google Drive", "", false, 10, 4, 1));

        checkList.add(new CheckItem("Double check senders email address for unexpected attachments", "", false, 10, 4, 1));

        checkList.add(new CheckItem("Close pop-up windows in the corner", "", false, 10, 4, 1));

        checkList.add(new CheckItem("Use a platform like Ubuntu or Chrome", "", false, 10, 4, 1));

        checkList.add(new CheckItem("If you find malware", "", false, 0, 4, 1));

        checkList.add(new CheckItem("Unplug your computer", "", false, 21, 4, 1));

        checkList.add(new CheckItem("Stop using your computer", "", false, 21, 4, 1));

        checkList.add(new CheckItem("Log into a safe computer and change all your passwords", "", false, 21, 4, 1));

        checkList.add(new CheckItem("Reinstall operating system", "", false, 21, 4, 1));

        checkList.add(new CheckItem("Take your computer to a security expert", "", false, 21, 4, 1));

        checkList.add(new CheckItem("Only reinstall files from before date of infection", "", false, 21, 4, 1));

        checkList.add(new CheckItem("Running anti-virus", "", false, 0, 4, 2));

        checkList.add(new CheckItem("Run only one anti-virus at a time", "", false, 28, 4, 2));

        checkList.add(new CheckItem("Update your anti-virus regularly", "", false, 28, 4, 2));

        checkList.add(new CheckItem("Enable your anti-virus’s 'always on' virus-detection feature", "", false, 28, 4, 2));

        checkList.add(new CheckItem("Scan your files regularly", "", false, 28, 4, 2));

        checkList.add(new CheckItem("Preventing infection", "", false, 0, 4, 2));

        checkList.add(new CheckItem("Open attachments via applications", "", false, 33, 4, 2));

        checkList.add(new CheckItem("Check where URLs lead", "", false, 33, 4, 2));

        checkList.add(new CheckItem("Disable ‘AutoPlay’", "", false, 33, 4, 2));

        checkList.add(new CheckItem("Prevent auto-running in your browser", "", false, 33, 4, 2));

        checkList.add(new CheckItem("Download over SSL", "", false, 33, 4, 2));

        checkList.add(new CheckItem("Use open-source software", "", false, 33, 4, 2));

        checkList.add(new CheckItem("Use open-source operating platform", "", false, 33, 4, 2));

        checkList.add(new CheckItem("Preventing untrusted connections", "", false, 0, 4, 2));

        checkList.add(new CheckItem("Turn on your firewall", "", false, 41, 4, 2));

        checkList.add(new CheckItem("Keep only essential programs", "", false, 41, 4, 2));

        checkList.add(new CheckItem("Create a strong password", "", false, 0, 5, 1));

        checkList.add(new CheckItem("Make it long", "", false, 44, 5, 1));

        checkList.add(new CheckItem("Make it complex", "", false, 44, 5, 1));

        checkList.add(new CheckItem("Make sure it’s not personal", "", false, 44, 5, 1));

        checkList.add(new CheckItem("Keep it secret", "", false, 44, 5, 1));

        checkList.add(new CheckItem("Make it unique", "", false, 44, 5, 1));

        checkList.add(new CheckItem("Change it regularly", "", false, 44, 5, 1));

        checkList.add(new CheckItem("Make it memorable", "", false, 44, 5, 1));

        checkList.add(new CheckItem("Give fake answers to security questions", "", false, 44, 5, 1));

        checkList.add(new CheckItem("Check the strength of passwords on Passfault", "", false, 0, 5, 1));

        checkList.add(new CheckItem("Change old or weak passwords", "", false, 0, 5, 1));

        checkList.add(new CheckItem("Change weak answers to security questions", "", false, 0, 5, 1));

        checkList.add(new CheckItem("Install KeePass", "", false, 0, 5, 2));

        checkList.add(new CheckItem("Create strong password for KeepPass", "", false, 0, 5, 2));

        checkList.add(new CheckItem("Back-up KeePass database", "", false, 0, 5, 2));

        checkList.add(new CheckItem("Ensure computer has no malware", "", false, 0, 5, 2));

        checkList.add(new CheckItem("Install two-step authentication", "", false, 0, 5, 2));

        checkList.add(new CheckItem("Create & maintain non-sensitive data account", "", false, 0, 5, 3));

        checkList.add(new CheckItem("Make non-sensitive account the primary account on your computer", "", false, 0, 5, 3));

        checkList.add(new CheckItem("Hide/protect your password manager", "", false, 0, 5, 3));

        checkList.add(new CheckItem("Check your rights about disclosure of information", "", false, 0, 5, 3));

        checkList.add(new CheckItem("Set up TrueCrypt", "", false, 0, 6, 2));

        checkList.add(new CheckItem("Dismount your TrueCrypt volume when not using files", "", false, 0, 6, 2));

        checkList.add(new CheckItem("Create a secret volume for most sensitive material", "", false, 0, 6, 2));

        checkList.add(new CheckItem("Rename your TrueCrypt volume with a different file extension", "", false, 0, 6, 2));

        checkList.add(new CheckItem("Rename the TrueCrypt program", "", false, 0, 6, 2));

        checkList.add(new CheckItem("If encryption is illegal…", "", false, 0, 6, 2));

        checkList.add(new CheckItem("Store only non-confidential information", "", false, 70, 6, 2));

        checkList.add(new CheckItem("Use code words", "", false, 70, 6, 2));

        checkList.add(new CheckItem("Store sensitive information in a secure webmail account", "", false, 70, 6, 2));

        checkList.add(new CheckItem("Store sensitive information securely off your computer", "", false, 70, 6, 2));

        checkList.add(new CheckItem("If Windows user, install Eraser", "", false, 0, 7, 1));

        checkList.add(new CheckItem("Securely delete individual files as needed", "", false, 0, 7, 1));

        checkList.add(new CheckItem("Securely delete all previously deleted data every month", "", false, 0, 7, 1));

        checkList.add(new CheckItem("If getting rid of old hardware, wipe the hard drive", "", false, 0, 7, 1));

        checkList.add(new CheckItem("If throwing out CDs, shred them", "", false, 0, 7, 1));

        checkList.add(new CheckItem("Encrypt SSD, USBs and SD cards", "", false, 0, 7, 1));

        checkList.add(new CheckItem("Make a table with info type, device and location", "", false, 0, 8, 2));

        checkList.add(new CheckItem("Identify your vulnerable information", "", false, 0, 8, 2));

        checkList.add(new CheckItem("Figure out what storage device is right for you", "", false, 0, 8, 2));

        checkList.add(new CheckItem("Install Cobian Backup", "", false, 0, 8, 2));

        checkList.add(new CheckItem("Organise your files to back up", "", false, 0, 8, 2));

        checkList.add(new CheckItem("Back up information on regular schedule", "", false, 0, 8, 2));

        checkList.add(new CheckItem("Install Recuva", "", false, 0, 8, 2));

        checkList.add(new CheckItem("If recovering files, do as little as possible before using Recuva", "", false, 0, 8, 1));

        return checkList;
    }

    public static ArrayList<Category> getCategoryList() {
        categoryList.add(new Category(1, 0, "My Security"));
        categoryList.add(new Category(2, 0, "Information Security"));
        categoryList.add(new Category(3, 2, "Managing Information"));
        categoryList.add(new Category(4, 2, "Malware"));
        categoryList.add(new Category(5, 2, "Passwords"));
        categoryList.add(new Category(6, 2, "Protecting Files"));
        categoryList.add(new Category(7, 2, "Safely Deleting"));
        categoryList.add(new Category(8, 2, "Backing Up"));
        categoryList.add(new Category(9, 0, "Communications Security"));
        categoryList.add(new Category(10, 0, "Tools"));
        categoryList.add(new Category(11, 0, "Personal Security"));
        categoryList.add(new Category(12, 0, "Travel Security"));
        categoryList.add(new Category(13, 0, "Operational Security"));
        categoryList.add(new Category(14, 0, "Organisational Security"));
        categoryList.add(new Category(15, 0, "Home / Office"));
        categoryList.add(new Category(16, 0, "Network Security"));
        categoryList.add(new Category(17, 0, "Glossary"));
        categoryList.add(new Category(18, 0, "Index"));
        return categoryList;
    }

}
