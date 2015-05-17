package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

public class TourSlideFragment extends Fragment {

    private int mPageNumber, mOffset;
    private Global global;
    OnNavigateToMainListener mCallback;

    public interface OnNavigateToMainListener {
        void onNavigationRequested();
    }

    public static TourSlideFragment create(int pageNumber, Global global) {
        TourSlideFragment fragment = new TourSlideFragment();
        fragment.global = global;
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnNavigateToMainListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNavigateToMainListener");
        }
    }

    public TourSlideFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_slide, container, false);

        LinearLayout slideLayout = (LinearLayout) rootView.findViewById(R.id.slide_layout);
        LinearLayout umbrellaLayout = (LinearLayout) rootView.findViewById(R.id.umbrella_layout);

        TextView headingTitle = (TextView) rootView.findViewById(R.id.heading_title);
        TextView headingBody = (TextView) rootView.findViewById(R.id.heading_body);
        TextView termsText = ((TextView) rootView.findViewById(R.id.text_terms));
        final Button skipBtn = (Button) rootView.findViewById(R.id.btn_skip);
        final ScrollView termsView = (ScrollView) rootView.findViewById(R.id.scroll_terms);
        headingBody.setPadding(UmbrellaUtil.dpToPix(25, getActivity()), UmbrellaUtil.dpToPix(40, getActivity()),UmbrellaUtil.dpToPix(25, getActivity()),0);

        switch (mPageNumber) {
            case 0:
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_purple));
                headingBody.setText("Umbrella makes your security simple");
                headingTitle.setVisibility(View.GONE);
                break;
            case 1:
                ((ImageView) rootView.findViewById(R.id.tour_image)).setImageResource(R.drawable.walktrough2);
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_green));
                headingTitle.setVisibility(View.GONE);
                headingBody.setText("Get advice on everything from sending an secure email to safe travel");
                break;
            case 2:
                ((ImageView) rootView.findViewById(R.id.tour_image)).setImageResource(R.drawable.walktrough3);
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_yellow));
                headingTitle.setVisibility(View.GONE);
                headingBody.setText("Use checklists to mark your progress");
                break;
            case 3:
                ((ImageView) rootView.findViewById(R.id.tour_image)).setImageResource(R.drawable.walktrough2);
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_purple));
                headingTitle.setVisibility(View.GONE);
                headingBody.setText("Stay up to date with the latest information on where you are");
                break;
            case 4:
                termsView.setVisibility(View.VISIBLE);
                umbrellaLayout.setVisibility(View.GONE);
                headingBody.setVisibility(View.GONE);
                headingTitle.setVisibility(View.VISIBLE);
                skipBtn.setVisibility(View.VISIBLE);
                skipBtn.setEnabled(false);
                skipBtn.setText("");
                slideLayout.setBackgroundColor(getResources().getColor(R.color.dashboard_light_blue));
                headingTitle.setText("Thank you");
                headingTitle.setGravity(Gravity.LEFT);
                termsText.setText("Umbrella would not be possible without the work, advice, friendship and cups of tea from our friends at:\n\nAmnesty International\nAshoka Foundation\nCARE International\nCentre for Safety and Development\nCommittee to Project Journalists\nDart Center for Journalism and Trauma\nElectronic Frontier Foundation\nEuropean Commission's Humanitarian Aid and Civil Protection Department\nEuropean Interagency Security Forum\nFrontline Defenders\nHumanitarian Response\niiLab\nInternews\nLevelUp\nOpen Technology Fund\nOverseas Development Institute\nProtection International\nRory Peck Trust\nSmall World News\nTactical Technology Collective\nThe Engine Room\nThe Guardian Project\nVidere\nAnd many other wonderful people!");
                termsText.setTextColor(getResources().getColor(R.color.white));
                termsText.setBackgroundResource(android.R.color.transparent);
                break;
            case 5:
                termsView.setVisibility(View.VISIBLE);
                umbrellaLayout.setVisibility(View.GONE);
                headingBody.setVisibility(View.GONE);
                headingTitle.setVisibility(View.VISIBLE);
                skipBtn.setVisibility(View.VISIBLE);
                termsView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        mOffset = termsView.getScrollY();
                        if (mOffset > 70) {
                            if (skipBtn != null) {
                                skipBtn.setTextColor(getActivity().getResources().getColor(R.color.white));
                            }
                        }
                    }
                });
                skipBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOffset > 70) {
                            global.set_termsAccepted(true);
                            mCallback.onNavigationRequested();
                        } else {
                            Toast.makeText(getActivity(), "You have to read and accept terms and conditions to continue", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_yellow));
                headingTitle.setText("Terms and Conditions");
                termsText.setText(Html.fromHtml("<p><b>PLEASE READ THESE TERMS OF USE CAREFULLY BEFORE USING THE UMBRELLA APP ON YOUR DEVICE. YOUR INTENTION IS PARTICULARLY DRAWN TO THE DISCLAIMER AND LIMITATION OF LIABILITY PROVISIONS IN CLAUSE 9.</b></p><p>These terms of use (the “Terms”) are a legal agreement between you (“you”) and Global Security First Limited of 2 Woodberry Grove, London, N12 0DR, company number: 08737382  (“GSF”, “us”, or “we”) in respect of your use of the Umbrella app (the “App”).By clicking on the \"Accept\" button below you agree to these Terms. If you do not agree to these Terms, we do not grant you permission to use the App and you must not use it and you must uninstall it from your device. </p><p>You must be over 18 in order to use the App.</p><p>You should print a copy of these Terms for your future reference.</p><big><b>YOUR ACCESS TO THE APP</b></big><p>Your use of the App under these Terms is also subject to any terms, rules or policies of any app store provider and/or operator (“App Store Provider”) from whom you have downloaded the App (“App Store Terms”). In the event of any conflict between these Terms and any App Store Terms, the App Store Terms will prevail. You and we acknowledge and agree that the relevant App Store Provider is a third party beneficiary under these Terms and will have the right to enforce these Terms against you directly.</p><p>We and our service providers are responsible for any maintenance and support of the App and you acknowledge that any App Store Provider from whom you download the App is not under any obligation to you to carry out any maintenance and/or support for the App itself. You acknowledge that we are under no obligation to carry out maintenance and/or support of the App and do so entirely at our discretion.</p><p>These Terms apply to the App including any updates or supplements to the App, unless such updates or supplements are accompanied by separate terms, in which case those terms apply. We may change these Terms at any time by notifying you of a change when you next start the App. The updated Terms will be displayed on-screen and you will be required to agree to them to continue your use of the App. The date these Terms were last updated appears at the bottom of these Terms.</p><p>From time to time we may issue updates to the App. Depending on the update, you may not be able to use the App until you have downloaded and installed the latest version of the App and accepted any new terms. Some updates may not be available to certain models of device. In order to use the App, you may be required to obtain certain updates and/or upgrades to your device. You are responsible for any costs and/or fees associated with any such updates/upgrades. You also understand and accept that: (a) the device you use to access the App will require certain software in order for the App to work correctly and it is your responsibility to ensure that you have the required up-to-date software, and (b) the App has not been developed to meet your individual requirements, and that it is therefore your responsibility to ensure that the functionality of the App meets your requirements.</p><p>You may only download and install the App onto a device that you own, or if you do not own the device, you must have the permission from the owner(s) to do so. You accept responsibility under these Terms for the use of the App, whether or not you own the device onto which the App is downloaded and installed. You also acknowledge that the owner(s) of the device onto which the App is installed may be charged by any relevant mobile network operator and/or internet service provider in relation to any data and/or mobile connectivity used by the App.</p><p>Access to the App may be suspended temporarily and without notice: (a) in the case of system failure, (b) for maintenance or repair, (c) where we reasonably suspect there has been a breach of these Terms, (d) for reasons beyond our control, or (e) as otherwise explained in these Terms.</p><big><b>PERMITTED USE AND RESTRICTIONS</b></big><p>In consideration of you agreeing to abide by these Terms, we grant you a non-transferable, non-exclusive licence (i.e. permission) to use the App subject to these Terms. We reserve all other rights, which are not granted in these Terms. You may download and install the App through authorised App Store Providers only, and you may view, use and display the App for your personal purposes only.</p><p>Except as explained in these Terms or as permitted by any local law, you agree: (a) not to copy the App except where such copying is incidental to normal use of the App, or where it is necessary for the purpose of back-up, (b) not to rent, lease, sub-license (i.e. grant anyone else the permission to use the App), loan, translate, merge, adapt, vary or modify the App (or any part of it), (c) not to, nor attempt to, disassemble, decompile, reverse-engineer or create derivative works based on the whole or any part of the App, and (d) not to provide or otherwise make available the App in whole or in part in any form to any person without prior written consent from us. Notwithstanding the above, the use, reproduction and distribution of components of the App licensed under an open source software license are governed solely by the terms of that open source software license and not these Terms. </p><p>You must not use the App: (a) in any unlawful manner, for any unlawful purpose, or to act fraudulently or maliciously, for example, by hacking into or inserting malicious code, including viruses, or harmful data, into the App or any operating system used by the App, (b) in a way that could damage, disable, overburden, impair or compromise our systems or security or interfere with other users, (c) to collect or harvest any information or data from the App or our systems or attempt to decipher any transmissions to or from the servers running the App, (d) to send, knowingly receive, upload, download, use or re-use any material which does not comply with these Terms, or (e) to transmit, or procure the sending of, any unsolicited or unauthorised advertising or promotional material or any other form of similar solicitation (spam).</p><big><b>SECURITY AND PASSWORDS</b></big><p>If you create a password or any other authentication information as part of our security procedures, you must treat such information as confidential. You must not disclose it to anyone else and must take appropriate steps to keep your information secure by not using an obvious password and ensuring that you keep your password confidential and change it regularly. If you know or suspect that anyone other than you knows your password or any other authentication information, you must promptly notify us using the contact details below. You are entirely responsible for all activities that occur through use of your password, even if another person was using [your account] at the relevant time. We are not responsible for any losses or liabilities arising out of or in connection with any unauthorised use of the App. </p><p>We have the right to disable any password or other authentication information whether chosen by you or allocated by us, at any time, if in our reasonable opinion you have failed to comply with any of the provisions of these Terms. In the event that you forget your password you may be required to delete the App, reinstall it and set up a new password in order to regain access to the App. Alternatively, if your password was provided by GSF, you may be able to notify us of its loss by email and we may be able to reset it.</p><big><b>YOUR PROMISES TO US</b></big><p>You promise to us that: (a) you are authorised to agree to these Terms, (b) you are not listed on any United States government list of prohibited or restricted parties, (c) you are not subject to and do not work for any company, organisation or other body that is subject to any United States, European Union or United Nations sanction, (d) you will not use the App for the purposes of or in connection with any act of terrorism or any other unlawful act, (e) any information submitted by you shall be at your own risk, and (f) you will not disclose nor allow to be disclosed by any means any confidential information belonging to us that you become aware of.</p><big><b>PRIVACY</b></big><p>These Terms also incorporate the terms of our privacy policy (as updated from time to time), which is available from within the App and on our website at https://secfirst.org/legal.html (the “Privacy Policy”) unless any element of the App is subject to a separate privacy policy, which we notify to you. Our Privacy Policy explains how your personal information will be collected and used as well as other information regarding your privacy (such as how you can adjust your privacy settings).</p><p>By agreeing to these Terms, you are also agreeing to the Privacy Policy and you consent to (i) the processing of your personal information as explained in the Privacy Policy and (ii) the collection of information from your device as explained in the Privacy Policy.</p><big><b>INTELLECTUAL PROPERTY RIGHTS</b></big><p>You acknowledge that all intellectual property rights in the App anywhere in the world belong to us or our licensors, that rights in the App are licensed (not sold) to you, and that you have no rights in, or to, the App other than the right to use it in accordance with these Terms.</p><p>In the event that anyone brings a claim that the App or any part of it, or your possession and/or use, infringes a third party’s intellectual property rights, we (and not any third party App Store Provider, including without limitation Google) shall be responsible for the investigation, defence, settlement and discharge of any such claim.</p><big><b>USER CONTENT</b></big><p>Whenever you make use of a feature that allows you to upload any content such as any text, audio, video, or other content via the App (“User Content”), or to share any User Content with other users of the App, you promise that any such User Content: (a) will not be defamatory, obscene, offensive or otherwise objectionable, (b) will not infringe the intellectual property rights (such as copyright) or other rights (such as privacy or confidentiality) of any third party, (c) will comply with applicable law in the UK and in any country from which it is posted, (d) will not promote violence or discrimination based on race, sex, religion, nationality, disability, sexual orientation or age, (e) will not be likely to deceive any person, (f) will not be threatening, abusive or cause annoyance, inconvenience or needless anxiety, (g) will not be likely to harass, upset, embarrass, alarm or annoy any other person, (h) will not impersonate any person, or misrepresent your identity or affiliation with any person, (i) will not give the impression that it emanates from us, if this is not the case, and (j) will not advocate, promote or assist any unlawful act such as (by way of example only) copyright infringement or computer misuse.</p><p>You acknowledge that any User Content you upload could pose us a significant risk if it breaches these Terms, and you therefore, to the extent permitted by law, agree to compensate us for any loss we suffer if anyone makes a claim against us due to any of your User Content. We will not be responsible, or liable to any third party, for any User Content submitted by you or any other user of the App.</p><p>Any User Content you upload to the App and any feedback or suggestions you provide to us regarding the App will be considered non-confidential and non-proprietary, and we have the right (subject to our Privacy Policy) to use, copy, distribute and disclose it to third parties for any purpose without limit in time and without payment to you. Whilst we may not actively monitor any User Content, we have the right (but are under no obligation) to remove any User Content if, in our opinion, it is in breach of these Terms or is otherwise inappropriate.</p><big><b>THIRD PARTY SITES AND SERVICES</b></big><p>The App may contain links to third party websites where you can access more information (“Third Party Sites”). Your use of any Third Party Sites will be governed by their terms and conditions and privacy policies (if any) (“Third Party Terms”). It is your responsibility to read the Third Party Terms. If you do not understand or agree to be bound by any Third Party Terms, you should not use any Third Party Sites. </p><p>We are not responsible for the accuracy of information/advice provided by a Third Party Site or any information/advice available through a link to a Third Party Site. Any reliance on information/advice provided by a Third Party Site is undertaken at your own risk.</p><big><b>DISCLAIMER AND LIMITATION OF LIABILITY</b></big><p>You acknowledge that you may be using the App in locations or situations where there may be an inherent risk of physical danger. The App provides information/advice collated from a number of different sources for information and reference purposes only. This information/advice may conflict in places, and may not be up-to-date or reliable, particularly in emergency situations. You must (a) always consider the potential risks involved in any given situation, (b) make use of a wide range of information/advice, security and risk-management resources available to you (i.e. not just the App), and (c) always exercise your own informed and reasonable judgment regarding the best course of action to follow. You acknowledge and agree that any information/advice provided by the App is provided for information purposes only, may not be appropriate to your particular circumstances and should not be considered information/advice on which you can rely. You further acknowledge and agree that any actions taken by you, whether in reliance on the App or otherwise, are by your decision alone and are undertaken entirely at your own risk. We are not responsible for any reliance you place on the information/advice contained within the App and any resulting action that you may choose to take. Accordingly, we are not responsible or liable for any physical injuries (including death) or damages you may sustain in connection with any action you take as a result of your use of, inability to use or your reliance upon the App, any of its features or any third-party applications or sites to which the App provides access.  </p><p>We provide the App on an “as is” and “as available” basis. We make no guarantee that the App will be uninterrupted, error free, or free from viruses or other harmful components. While we take reasonable precautions to prevent the existence of computer viruses and/or other malicious programs, we accept no liability for them. We also make no promises or guarantees, whether express or implied, that the content within or made available via the App is accurate, complete or up-to-date. To the extent permitted by law, we exclude all conditions, warranties, representations or other terms, which may apply to the App, whether express or implied.</p><p>We only supply the App for domestic, personal and private use. You agree not to use the App for any commercial, business or resale purposes, and we have no liability to you for any loss of profit, loss of business, business interruption, or loss of business opportunity. We also do not accept any liability or responsibility for any loss (howsoever caused) arising out of or in connection with: (a) any damage to your device, or (b) any reliance placed on any content displayed on the App.</p><p>Our maximum liability under or in connection with these Terms whether in contract, tort (including negligence) or otherwise, shall in all circumstances be limited to £10. </p><p>Nothing in these Terms will limit or exclude our liability for: (a) death or personal injury resulting from our negligence, (b) fraud or fraudulent misrepresentation, and/or (c) any other liability that cannot be excluded or limited by applicable law. Furthermore, nothing in these Terms restricts your legal rights as a consumer.</p><big><b>TERMINATION</b></big><p>We may terminate these Terms and your permission to use the App immediately if: (a) you commit any breach of these Terms, (b) we discontinue the App, or (c) we are prevented from providing the App for any reason. </p><p>Furthermore, we reserve the right to change, edit, suspend delete and/or cancel any part of the App and/or your access to it at any time with or without notice to you: (a) if required by law, (b) due to an event beyond our control, or (c) as a result of changes, cancellations or revocation of approval by any applicable App Store Provider.</p><p>On termination of these Terms for any reason: (a) all rights granted to you under these Terms will immediately cease, (b) you must immediately cease all activities authorised by these Terms (including your use of the App), (c) you must immediately uninstall and remove the App from your device(s), and (d) you acknowledge that we may restrict your access to the App and/or remove it from your device.</p><big><b>COMMUNICATION BETWEEN US</b></big><p>If you wish to contact us in writing, or if any condition in these Terms require you to give us notice in writing, you can send this to us by e-mail or by prepaid post using the contact details at the bottom of these Terms. If we have to contact you or give you notice in writing, we will do so either by e-mail or by pre-paid post using the contact details you provide to us.</p><big><b>OTHER IMPORTANT TERMS</b></big><p>We may transfer our rights and obligations under these Terms to another organisation, but this will not affect your rights or our obligations under these Terms. </p><p>If we fail to insist that you perform any of your obligations under these Terms, or if we do not enforce any of our rights against you, or if we delay in doing so, that will not mean that we have waived any of our rights against you and will not mean that you do not have to comply with those obligations. If we do waive a default by you, we will only do so in writing, and that will not mean that we will automatically waive any later default by you. </p><p>Each of the conditions of these Terms operates separately. If any court or competent authority decides that any of them are unlawful or unenforceable, the remaining conditions will remain in full force and effect. Other than as expressly set out in these Terms, no one other than you and us is intended to have any right or ability to enforce any of the provisions of these Terms.</p><p>These Terms are governed by English law and the courts of England and Wales will have non-exclusive jurisdiction.</p><p>These Terms were last updated on 23rd February 2015.</p><big><b>ABOUT US</b></big><p>Global Security First Limited.<br>Registered Office Address: Ground Floor, 2 Woodberry Grove, London, N12 0DR, UK.<br>Company No: 08737382<br>Contact email address: info@secfirst.org</p>"));
                break;
        }

        return rootView;
    }


}