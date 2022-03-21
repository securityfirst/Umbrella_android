# Contributing to Umbrella

There are many ways you can contribute to Umbrella. 
No effort is too small and whatever you bring to this community will be appreciated. So read on to find out how you can make a difference in Umbrella. 

## Reporting bugs

Unfortunately stuff breaks sometimes. 
If you are in a hurry and have found a code or content problem then please email it to <support@secfirst.org>. 
If you have a little more time we generally try to manage any bugs using GitHub. 
Please look at the [existing issues](https://github.com/securityfirst/Umbrella_android/issues) for your bug and create a new one if the issue is not yet tracked.

If the issue you have identified is a security risk to users, please
[read the documentation about our responsible disclosure policy](https://secfirst.org/legal).

If you wish to contact us via PGP, please drop a mail to
<rory@secfirst.org> ([2C1D3B4D](https://pgp.mit.edu/pks/lookup?op=vindex&search=0xFFB9B5BE2C1D3B4D))

After reporting a bug on GitHub, if the Umbrella developers make a code change that resolves your issue, then your GitHub issue will typically be closed from the relevant patch message. 
If, after testing the fix, you find that it does not really fix your bug, please leave a comment on your issue explaining the situation. 
When you do, we will receive a notification and respond on your issue or reopen it (or both). 
Please do not create a duplicate issue.

In other cases, your issue may be closed with a specific resolution, such as `R: duplicate`, or `R: wontfix`. 
Each of these labels has a description that explains the label. 
We’ll also leave a comment explaining why we’re closing the issue with one of these specific resolutions. 
If the issue is closed without one of these specific resolutions, then it means, by default, that your reported bug was fixed or your requested enhancement was implemented

## Contributing Ideas

Ideas are powerful things! 
If you have any about what we could do better or things which you think we should do in future, please email us at info@secfirst.org. 
Alternatively, you may open a feature request as a GitHub issue. 

## Contributing Code

We have a really big development plan of functionality we want to include in the future and are currently in the process of building a way to manage contributions from the open source community. 
Until we have that up, please drop us a mail to <info@secfirst.org> if you are interested in a contributing a specific part of future code. 
If there is something you want to help out with in the interim, then here is some basic advice:

1.  Fork this repo!

2.  Create your feature branch: `git checkout -b my-new-feature`

3.  Commit your changes: `git commit -am 'Add some feature'`

4.  Push to the branch: `git push origin my-new-feature`

5.  Submit a pull request :D

**Build Instructions**

[Build Umbrella from source](BUILD.md)

## Writing content for Umbrella

Depending on your technical knowledge, there are several ways to contribute content for Umbrella. 
If you just want to tell us about one tiny thing and you don’t have bandwidth for a tutorial right now, feel free to email a short description of the change you want to propose to  info@secfirst.org or [open an issue](https://github.com/securityfirst/umbrella-content/issues/new) in our [Umbrella content repo](https://github.com/securityfirst/umbrella-content).

### Using GitHub and Prose.io 

If you are not familiar with git, GitHub and Markdown, read our [beginner tutorial for editing content hosted in a GitHub repo using prose.io](https://docs.google.com/document/d/1y9TMmvkOh_DiVm6Qdw9imEHomGabcbHxJhHFVRtcXeQ/edit#).

### Using GitHub and Markdown
You can also use Markdown to can write new content for Umbrella or edit existing Umbrella content from GitHub's interface. 
Follow these steps:
- Fork the [umbrella-content](https://github.com/securityfirst/umbrella-content) repository
- [Create a new branch](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-and-deleting-branches-within-your-repository#creating-a-branch)
- Make your changes and commit
- Submit a [pull request](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/creating-a-pull-request-from-a-fork)

([This](https://daringfireball.net/projects/markdown/) is a great source for learning about Markdown.)


## Improving documentation

Umbrella's documentation, especially the [user manual](https://secfirst.org/umbrella/manual), would greatly benefit from your help. 
Good documentation is difficult to keep up-to-date. 
You can help us edit or write new content for the unser manual. 
In addition, if you find a typo or an error on any page of the [secfirst.org](https://secfirst.org/umbrella/) website please let us know — ideally, by submitting a pull request against the [secfirst repo](https://github.com/securityfirst/secfirst.org) with your correction.

If you have a question about something you read in the documentation, please send us a mail to <info@secfirst.org>.

Here are a few guidelines and Markdown conventions to keep in mind when writing documentation for Umbrella. Doing so helps creating a consistent language across the documentation:
- Do not duplicate documentation. Duplicating documentation is almost always a bad idea, since it presents a maintainance issue. Almost all documentation has to be updated as some point, and when similar documentation appears in more than one place, it is very easy for it to get updated in one place but not the others When this happens, this leads to inconsitent, and the outdated documentation becomes a trap, especially for novice users. The solution is to link to existing documentation rather than duplicating it. There are some exceptions to this policy (e.g., information that is certain not to change for a very long time), but they are rare.
- In order to enable offline browsing, always use relative (rather than absolute) links, e.g., /umbrella/manual/ instead of https://www.securityfirst.org/umbrella/manual/. An exception to this rule is when linking to files like README.md, CONTRIBUTING.md

## Translating Umbrella

You can help correct, improve, or complete the translations of Umbrella content into your native language. 
Most of Umbrella can be translated directly online, through a simple web interface, after logging in with [Transifex](https://www.transifex.com/otf/umbrella-app/). 
Umbrella can be translated into more than 140 languages this way.

To get started with using Transifex, you can [watch their introductory video](https://www.youtube.com/watch?v=3y0x8q3Oj7Q).
Note that only reviewed translations are included in Umbrella.

If you want to go further and translate the Umbrella website (particularly the user manual), please [get in touch](https://secfirst.org/contact/).

## Promoting Umbrella

### Outreach material
- [Logos and assets](https://github.com/securityfirst/secfirst.org/tree/master/static/imgs)
- [Press kit](https://drive.google.com/drive/folders/17IQfcnUWuf4jziGLoX-14yLqfzsr70vR)
- [Press and media information](https://secfirst.org/press/)

### Talking at events

You can talk about Umbrella at various events, e.g. a journalist safety workshop, a group of domestic violence survivors, a security conference. 
It all depends on who you happen to meet, what they are interested in, and what you feel comfortable talking about. 
Do not hesitate asking for advice or review of your material.
Discuss any promotional ideas you may have with us at [info@securityfirst.org](mailto:info@securityfirst.org).

## Improving Umbrella's user experience

If you are a UI/UX or HCI person, you can surely find ideas to improve Umbrella! 
Even if you don't implement your suggestions yourself, create GitHub issues with your ideas so that others can benefit from your insight.

