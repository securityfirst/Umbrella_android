package org.secfirst.umbrella.data;

import org.secfirst.umbrella.models.Segment;

import java.util.ArrayList;

public class InitialData {

    private static ArrayList<Segment> segmentList = new ArrayList<Segment>();
    public static final int INTERNET_SECURITY = 1;
    public static final int STAYING_ANONYMOUS = 2;
    public static final int MOBILE_PHONES = 3;
    public static final int BACKING_UP = 4;
    public static final int PHYSICAL_ACCESS = 5;

    public static ArrayList<Segment> getSegmentList() {
        setInternetSecurity();
        setStayingAnonymous();
        setMobilePhones();
        setBackingUp();
        setPhysicalAccess();
        return segmentList;
    }

    private static void setInternetSecurity() {
        segmentList.add(new Segment("Internet Security", "", "If you are not able to log in to your account to change the passwords, consider getting in contact with your email provider to try to reclaim your account. Some email providers have special procedures in place to help users in such situation. It is helpful to know these procedures ahead of time.", INTERNET_SECURITY));

        segmentList.add(new Segment("Mitigate information loss and impact to your community. It is important to make a response plan. Knowing what sensitive kinds of information you had in your account and determining the persons with whom you exchange information via that account, decide whom you should alert and what other accounts you will have to revisit or close. Determine what services (web, financial, etc.) you need to revisit or cancel. It is important that you check the folders of your account (if you can) to research on what could have been sent from your account and to act accordingly. To inform your contacts you will need to keep a separate backup of your address book. Also review your account settings to see possible changes that has been made. Check accounts signature option for links and malware, forwarding options that would allow to copy emails that you receive to third account, away message, display name, etc.", INTERNET_SECURITY));

        segmentList.add(new Segment("Research how your account was compromised. Was it because of having a weak password, or due to malware infection, etc. The more you will establish about this, the better you will be able to respond to the situation and better you will be able to protect your contacts.", INTERNET_SECURITY));

        segmentList.add(new Segment("Review security of all of your devices that access emails from this account, and devices on which you stored the password to this email account. See chapters 1. How to protect your computer from malware and hackers, 2. How to protect your information from physical threats and 11. How to use smartphones as securely as possible. Review your anti-virus software (see the Avast! - Anti-Virus and Spybot - Anti-Spyware hands-on guides). Scan your computer: read 4.1 A Short Guide to Dealing with Virus Outbreaks. Use a rescue CD or USB to do a thorough scan - read 4.9 Advanced Virus Removal Methods. If you are not certain that you are able to clean your device, consider reinstalling all software including the operating system from a clean source. Consider switching to more secure programs like Firefox, Thunderbird, LibreOffice and other Free and Open Source Programs. After making the above improvements to the security of your devices, change your account passwords again to new, stronger ones.", INTERNET_SECURITY));

        segmentList.add(new Segment("", "Advanced Email Security", "The tools and concepts discussed below are recommended for experienced computer users.", INTERNET_SECURITY));

        segmentList.add(new Segment("", "Using Public Key Encryption in Email", "It is possible to achieve a greater level of email privacy, even with an unsecured email account. In order to do this, you will need to learn about public key encryption. This technique allows you to encode individual messages, making them unreadable to anyone but the intended recipients. The ingenious aspect of public key encryption is that you do not have to exchange any secret information with your contacts about how you are going to encode messages in the future.", INTERNET_SECURITY));

        segmentList.add(new Segment("<strong>Pablo: But how does all this work?</strong>", INTERNET_SECURITY));

        segmentList.add(new Segment("Claudia: Clever mathematics! You encode messages to a given email contact using her special 'public key', which she can distribute freely. Then, she uses her secret 'private key', which she has to guard carefully, in order to read those messages. In turn, your contact uses your public key to encrypt messages that she writes to you. So, in the end, you do have to exchange public keys, but you can share them openly, without having to worry about the fact that anybody who wants your public key can get it.", INTERNET_SECURITY));

        segmentList.add(new Segment("This technique can be used with any email service, even one that lacks a secure communication channel, because individual messages are encrypted before they leave your computer.", INTERNET_SECURITY));

        segmentList.add(new Segment("Remember that by using encryption, you could attract attention to yourself. The type of encryption used when you access a secure website, including a webmail account, is often viewed with less suspicion than the type of public key encryption being discussed here. In some circumstances, if an email containing this sort of encrypted data is intercepted or posted on a public forum, it could incriminate the person who sent it, regardless of the content of the message. You might sometimes have to choose between the privacy of your message and the need to remain inconspicuous.", INTERNET_SECURITY));

        segmentList.add(new Segment("", "Encrypting and Authenticating Individual Messages", "Public key encryption may seem complicated at first, but it is quite straightforward once you understand the basics, and the tools are not difficult to use. Simple, user-friendly and portable, the gpg4usb program can encrypt email messages and files even when you are not connected to the Internet.", INTERNET_SECURITY));

        segmentList.add(new Segment("Hands-on: Get started with the Portable gpg4usb - email text and files encryption program guide", INTERNET_SECURITY));

        segmentList.add(new Segment("The Mozilla Thunderbird email program can be used with an extension called Enigmail to encrypt and decrypt email messages quite easily.", INTERNET_SECURITY));
    }

    private static void setStayingAnonymous() {
        segmentList.add(new Segment("Staying Anonymous", "", "", STAYING_ANONYMOUS));

        segmentList.add(new Segment("", "Specific circumvention proxies", "VPN proxies listed below make your entire Internet connection pass through the proxy while you are \"connected\". This can be helpful if you use email or instant messaging providers that are filtered in your country", STAYING_ANONYMOUS));

        segmentList.add(new Segment("Riseup VPN. It is for users who have email accounts on the Riseup server. The collective offers the possibility of connecting to a secure, private, free VPN proxy server. Please read more about Riseup VPN and on how to connect to it.", STAYING_ANONYMOUS));

        segmentList.add(new Segment("Your-Freedom is a private, secure, VPN/SOCKS circumvention proxy. It is a freeware tool that can be used to access a free circumvention service. There are restrictions on bandwidth and for how long can you can use it (3 hours per day, up to 9 hours per week). You can also pay a fee to access a commercial service, which is faster and has fewer limitations. In order to use Your-Freedom, you will need to download the tool and create an account, both of which can be done at the Your-Freedom website. You will also need to configure your browser to use the OpenVPN proxy when connecting to the Internet. You can read more in Your-Freedom documentation.", STAYING_ANONYMOUS));

        segmentList.add(new Segment("Freegate is a public, secure, VPN, freeware circumvention proxy. You can download the latest version of Freegate or read interesting article about it.", STAYING_ANONYMOUS));

        segmentList.add(new Segment("SecurityKISS is a public, secure, VPN, freeware circumvention proxy. To use it you need to download and run a free program. There is no need to register an account. Free users are restricted to a 300 MB per day usage limit and by higher Internet traffic through the proxy. Paid subscription offers restriction-free usage and more VPN servers. Please see the SecurityKISS homepage to learn more.", STAYING_ANONYMOUS));

        segmentList.add(new Segment("Psiphon3 is a secure, public circumvention tool that utilizes VPN, SSH and HTTP Proxy technology to provide you with uncensored access to Internet content. In order to use it you need to download the program from the Psiphon3 homepage and run it to select which mode you would like to use VPN, SSH, SSH+. Psiphon3 works with Android devices as well. Please see the homepage to learn more.", STAYING_ANONYMOUS));

        segmentList.add(new Segment("", "Web Proxies:", "Peacefire maintains a large number of public, web-based proxies, which can be secure or insecure, depending on how you access them. When using a Peacefire proxy, you must enter the HTTPS address in order to have a secure connection between yourself and the proxy. New proxies are announced to a large mailing list on a regular basis. You can sign up to receive updates at the Peacefire website.", STAYING_ANONYMOUS));
    }

    private static void setMobilePhones() {
        segmentList.add(new Segment("Mobile Phones", "", "", MOBILE_PHONES));

        segmentList.add(new Segment("", "Text based communications – SMS / Text messages", "You should not rely on text message services to transmit sensitive information securely. The messages exchanged are in plain text which makes them inappropriate for confidential transactions.", MOBILE_PHONES));

        segmentList.add(new Segment("<strong>Borna:</strong> What if I never make calls on my mobile, and only send and receive these small messages. They can't listen in on something if no one is saying anything, and it is very quick, no?", MOBILE_PHONES));

        segmentList.add(new Segment("<strong>Delir:</strong> Wait a minute. These messages are also easy enough to intercept, and anyone with access to the traffic from the phone company, or even other people with the right equipment, can capture and read these messages which are moving around the network in plain text, being saved from one tower to the next.", MOBILE_PHONES));

        segmentList.add(new Segment("<strong>Borna:</strong> That's just silly. What should I do? Write in code like we did during the war?", MOBILE_PHONES));

        segmentList.add(new Segment("<strong>Delir:</strong> Well, sometimes the oldest shoes are the most comfortable ones.", MOBILE_PHONES));

        segmentList.add(new Segment("Sent SMS messages can be intercepted by the service operator or by third parties with inexpensive equipment. Those messages will carry the phone numbers of the sender and recipient as well as the content of the message. What's more, SMS messages can easily be altered or forged by third parties.", MOBILE_PHONES));

        segmentList.add(new Segment("Consider establishing a code system between you and your recipients. Codes may make your communication more secure and may provide an additional way of confirming the identity of the person you\\'re communicating with. Code systems need to be secure and change frequently.", MOBILE_PHONES));

        segmentList.add(new Segment("SMS messages are available after transmission:", MOBILE_PHONES));

        segmentList.add(new Segment("In many countries, legislation (or other influences) requires the network providers to keep a long-term record of all text messages sent by their customers. In most cases SMS messages are kept by the providers for business, accounting or dispute purposes.", MOBILE_PHONES));

        segmentList.add(new Segment("Saved messages on your phone can easily be accessed by anybody who gets hold of your phone. Consider deleting all received and sent messages straightaway.", MOBILE_PHONES));

        segmentList.add(new Segment("Some phones have the facility to disable the logging of phone-call or text-message history. This would be especially useful for people doing more sensitive work. You should also make sure that you are familiar with what your phone is capable of. Read the manual! Functions beyond speech and messages. Mobile phones are turning into mobile computing devices, complete with their own operating systems and downloadable applications that provide various services to the user.", MOBILE_PHONES));

        segmentList.add(new Segment("Chapter 11: How to use smartphones as securely as possible covers issues related to these kinds of mobile devices, as connectivity to internet brings about both potentials as well as risks we have covered in previous chapters.", MOBILE_PHONES));

        segmentList.add(new Segment("While some of the earlier mobile phone models have fewer or no internet functions, it is nevertheless important to observe the precautions outlined below on all phones. Also you should find out exactly what the capabilities of your phone are, in order to be certain that you have taken appropriate measures:", MOBILE_PHONES));

        segmentList.add(new Segment("Do not store confidential files and photos on your mobile phone. Move them, as soon as you can, to a safe location, as discussed in Chapter 4: How to Protect Files on Your Computer.", MOBILE_PHONES));

        segmentList.add(new Segment("Frequently erase your phone call records, messages, address book entries, photos, etc.", MOBILE_PHONES));

        segmentList.add(new Segment("If you use your phone to browse the internet, follow safe practices similar to those you use when you are on the computer (e.g. always send information over encrypted connection like HTTPS).", MOBILE_PHONES));

        segmentList.add(new Segment("Connect your phone to a computer only if you are sure it is malware free. See Chapter 1: How to Protect Your Computer From Malware and Hackers.", MOBILE_PHONES));

        segmentList.add(new Segment("Do not accept and install unknown and unverified programmes on your phone, including ring tones, wallpaper, java applications or any others that originate from an unwanted and unexpected source. They may contain viruses, malicious software or spying programmes.", MOBILE_PHONES));

        segmentList.add(new Segment("Observe your phone's behaviour and functioning. Look out for unknown programmes and running processes, strange messages and unstable operation. If you don't know or use some of the features and applications on your phone, disable or uninstall them if you can.", MOBILE_PHONES));

        segmentList.add(new Segment("Be wary when connecting to WiFi access points that don't provide passwords, just as you would when using your computer and connecting to WiFi access points. The mobile phone is essentially like a computer and thus shares the vulnerabilities and insecurities that affect computers and the internet.", MOBILE_PHONES));

        segmentList.add(new Segment("Make sure communication channels like Infrared (IrDA), Bluetooth and Wireless Internet (WiFi) on your phone are switched off and disabled if you are not using them. Switch them on only when they are required. Use them only in trusted situations and locations. Consider not using Bluetooth, as it is relatively easy to eavesdrop on this form of communication. Instead, transfer data using a cable connection from the phone to handsfree headphones or to a computer.", MOBILE_PHONES));
    }

    private static void setBackingUp() {
        segmentList.add(new Segment("Backing Up", "Recovering from accidental file deletion", "When you delete a file in Windows, it disappears from view, but its contents remain on the computer. Even after you empty the Recycle Bin, information from the files you deleted can usually still be found on the hard drive. See Chapter 6: How to destroy sensitive information to learn more about this. Occasionally, if you accidentally delete an important file or folder, this security vulnerability can work to your advantage. There are several programs that can restore access to recently-deleted files, including a tool called Recuva.", BACKING_UP));

        segmentList.add(new Segment("", "Hands-on: Get started with the Recuva - File Recovery Guide", "These tools do not always work, because Windows may have written new data over your deleted information. Therefore, it is important that you do as little as possible with your computer between deleting a file and attempting to restore it with a tool like Recuva. The longer you use your computer before attempting to restore the file, the less likely it is that you will succeed. This also means that you should use the portable version of Recuva instead of installing it after deleting an important file. Installing the software requires writing new information to the file system, which may coincidentally overwrite the critical data that you are trying to recover.", BACKING_UP));

        segmentList.add(new Segment("While it might sound like a lot of work to implement the policies and learn the tools described in this chapter, maintaining your backup strategy, once you have a system in place, is much easier than setting it up for the first time. And, given that backup may be the single most important aspect of data security, you can rest assured that going through this process is well worth the effort.", BACKING_UP));

    }

    private static void setPhysicalAccess() {
        segmentList.add(new Segment("Physical Access", "Creating your physical security policy", "Once you have assessed the threats and vulnerabilities that you or your organisation face, you must consider what steps can be taken to improve your physical security. You should create a detailed security policy by putting these steps in writing. The resulting document will serve as a general guideline for yourself, your colleagues and any newcomers to your organisation. It should also provide a checklist of what actions should be taken in the event of various different physical security emergencies. Everybody involved should take the time to read, implement and keep up with these security standards. They should also be encouraged to ask questions and propose suggestions on how to improve the document.", PHYSICAL_ACCESS));

        segmentList.add(new Segment("Your physical security policy may contain various sections, depending on the circumstances:", PHYSICAL_ACCESS));

        segmentList.add(new Segment("An office access policy that addresses the alarm systems, what keys exist and who has them, when guests are allowed in the office, who holds the cleaning contract and other such issues", PHYSICAL_ACCESS));

        segmentList.add(new Segment("A policy on which parts of the office should be restricted to authorized visitors", PHYSICAL_ACCESS));

        segmentList.add(new Segment("An inventory of your equipment, including serial numbers and physical descriptions", PHYSICAL_ACCESS));

        segmentList.add(new Segment("A plan for securely disposing of paper rubbish that contains sensitive information", PHYSICAL_ACCESS));

        segmentList.add(new Segment("Emergency procedures related to:<br><small>Who should be notified if sensitive information is disclosed or misplaced<br> - Who to contact in the event of a fire, flood, or other natural disaster<br> - How to perform certain key emergency repairs<br>How to contact the companies or organizations that provide services such as electrical power, water and Internet access<br> - How to recover information from your off-site backup system. You can find more detailed backup advice in Chapter 5: How to recover from information loss.<br>Your security policy should be reviewed periodically and modified to reflect any policy changes that have been made since its last review.<br> - And, of course, don't forget to back up your security policy document along with the rest of your important data. See the Further reading section for more information about creating a security policy.<br></small>", PHYSICAL_ACCESS));
    }
}
