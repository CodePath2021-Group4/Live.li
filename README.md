# LIVE.LI

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Live.li is a livestream application where users can discover, search, and watch their favorite YouTube livestreamers. Users can discover streamers based on their category, search for streamers with their own keyword, and follow these streamers to keep up with their streams.

### App Evaluation
- **Category:** Entertainment
- **Mobile:**  This app is primariy developed for mobile but it could also be used as a web application. All the apps main features would work on a web application, but the mobile version allows for additional functionality and unique features.
- **Story:** Allows users to find new streamers and watch their favorite content creators.
- **Market:** Anyone who watches livestreams or YouTube. Also those who have favorite YouTuber streamers and want to keep up with their content.
- **Habit:** This app can be used whenever a user wants to watch a stream or watch their favorite content creator. It also depends on how much the user dedicates their time to watching streamers or content creators.
- **Scope:** First we would start off with users being able to watch a stream whether its through their personal feed or their discovery feed, then this could evolve into a more social and interactive livestreaming application where users can communicate with one another and with the streamer. This has large potential to be used with the YouTube application and even as an integration with the YouTube application.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can login/logout
* User can sign up
* User can view a feed of streams
* User can search for streams using search bar
* User can search for streams using categories
* User can click on a stream to watch
* User can see the streamers name, title, profile image, video description when clicking on a stream
* User can follow/unfollow a streamer
* User can tab between 3 screens, a discover screen, a personal feed screen, and a profile screen
* User can go to the discover page to search/find streams
* User can go to their personal feed to see their followed streamers
* User can go to their profile page to see their username, profile image, and logout button
* User can manage the channels they follow on their profile page


**Optional Nice-to-have Stories**

* Livechat Feature
* Push notifications for when a followed channel goes live
* Users can update their profile image
* Users can customize their interface (Change from light mode to dark mode, etc.)
* Clicking on streamers name or profile image brings the user to their youtube channel
* YouTube Integration (Import the users subscriptions)


### 2. Screen Archetypes

* Login Screen
   * User can login
   * User can sign up
* Stream
   * User can view a feed of streams
   * User can search for streams using search bar
   * User can search for streams using categories
   * User can click on a stream to watch
* Detail
   * User can see the streamers name, title, profile image, and video description
   * User can follow/unfollow a streamer
* Profile
   * User can view their username and profile image 
   * User can logout
   * User can manage the channels they follow

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Discover Feed
* Personal Feed
* Profile Page

**Flow Navigation** (Screen to Screen)

* Login Screen
   * Stream Screen
* Stream Screen
   * Detail Screen (after clicking on a screen)
   * Profile Screen (after clicking the "profile" tab on the bottom of the screen)
* Detail Screen
    * Stream Screen (after user backs out of detail view)
* Profile Screen
    * Login Screen (after the user clicks the "logout" button)

## Wireframes
<img src="https://i.imgur.com/Kqux7Y7.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups
<img src="DigitalWireframe1.png" width=300>
<img src="DigitalWireframe2.png" width=300>
<img src="DigitalWireframe3.png" width=300>
<img src="DigitalWireframe4.png" width=300>

### [BONUS] Interactive Prototype
<img src="group_walkthrough.gif" width=300>

## Schema 
### Models
#### User
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | username      | String     | Username provided when User signed up (unique) |
   | password      | String   | Password provded when User signed up |
   | profile_image | File     | Profile picture uploaded by User (default field) |
   | channels_followed        | Array   | Will hold a list of Channel ID's followed by User ||

#### Stream
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | channel_name      | String     | Name of channel associated with a particular stream (unique) |
   | title      | String   | Current title of stream provided by streamer |
   | thumbnail | File     | Streamer preview |
   | streamer_image | File   | Profile Picture uploaded by Streamer |
   | start_time        | Array   | Time when stream began |
   | channel_id | String | Used for follow and unfollowing feature (unique) |
   | video_id | String | Used to display/show current stream (unique) |
   | view_count | String | Used to display current view count of stream | 
### Networking
- Login Screen
- - 
- Stream Screen
- Detail Screen
- Profile Screen
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
