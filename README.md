[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=plastic)](https://android-arsenal.com/api?level=16)
# AltiMeter 
*Android app for recording device's elevation and location*

## What is AltiMeter
AltiMeter is an android app with core features:
* recording user's location
* recording user's altitude
* using three different data sources ( GPS, network, pressure sensor) and combining them
* genereting real time diagrams with recorded altitude
* saving locations data into database
* gathering local and global statistics about recording sessions
* generating google maps
* sharing screenshots via social media and other services

## What was used to build app
To code and build this app was used:

**Tools**
* Java 7/8
* Android Studio 2.3
* SQLite

**Pattern**
* MVP

**Libraries**
* <a href="https://github.com/JakeWharton/butterknife" title="ButterKnife">ButterKnife</a>
* <a href="https://github.com/appsthatmatter/GraphView" title="GraphView">GraphView</a>
* <a href="https://github.com/ReactiveX/RxJava" title="GraphView">RxJava</a>

**Play services**
* location
* map

**Tested on**
* Nexus 4
* API version 22

## How does it look like
App is splitted into several different sections.

Navigation between activities and fragments is done through Navigarion Drawer | Main view section, consists lists of saved recording sessions
:-------------------------:|:-------------------------:
<img src="https://rawgit.com/GregoryIwanek/AltiMeter/GregoryIwanek-readme/screenshot/nav_drawer.png" title="nav drawer" height="250" />  |  <img src="https://rawgit.com/GregoryIwanek/AltiMeter/master/screenshot/main_view.png" title="main view" height="250" />

Recording session section, works as operation center for recording altitude and locations | Details section, shows basic information and statistics of choosen session
:-------------------------:|:-------------------------:
<img src="https://rawgit.com/GregoryIwanek/AltiMeter/GregoryIwanek-readme/screenshot/recording_session.png" title="recording session" height="250" />  |  <img src="https://rawgit.com/GregoryIwanek/AltiMeter/GregoryIwanek-readme/screenshot/details.png" title="details section" height="250" />

Statistics section, shows global statistics of the all recording sessions | Map section, shows recorded locations as a path on a google map
:-------------------------:|:-------------------------:
<img src="https://rawgit.com/GregoryIwanek/AltiMeter/GregoryIwanek-readme/screenshot/statistics.png" title="statistics section" height="250" />  |  <img src="https://rawgit.com/GregoryIwanek/AltiMeter/GregoryIwanek-readme/screenshot/map.png" title="map section" height="250" />

About section, consists series of fragments with descriptions of other sections | Settings section, holds bunch of custom user settings
:-------------------------:|:-------------------------:
<img src="https://rawgit.com/GregoryIwanek/AltiMeter/master/screenshot/about.png" title="about section" height="250" />  |  <img src="https://rawgit.com/GregoryIwanek/AltiMeter/master/screenshot/settings.png" title="settings section" height="250" />

## To do
* add settings switch theme ( dark/light)
* fix color of secondary text in settings popup windows
