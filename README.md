# DawgMaps

Built for the Hack'20 hackathon.

[Hackathon Submission](https://devpost.com/software/hack20-webapp)

### Contributors:

- [Abhay Deshpande](https://github.com/abhaybd)

- [Cindy Zou](https://github.com/x9du)
- [Yiyang (Ian) Wang](https://github.com/iwangy)
- [Abhinav Bandari](https://github.com/abx393)
- [Karthikeya Vemuri](https://github.com/Karkeys360)

## Inspiration

We were inspired by efforts to improve student safety and contact tracing on campus to create a heatmap that allows users to avoid the most crowded areas. We hope to help students engage with campus facilities and businesses while respecting social distancing guidelines. However, the app can be used anywhere, not just on college campuses!

## What it does

In response to the COVID-19 pandemic and the growing need for responsible social distancing, DawgMaps provides users with real-time information about large gatherings and crowds in their area to help users avoid them. We designed a native Android mobile app to crowdsource real time location data, compile it into a cloud database, and display it on a webapp that generates population density heat maps. The native app displays the webapp and collects all the other information in the background allowing users access to the heatmaps on any platform. Additionally, the native app has functionality that detects when a user enters a sufficiently dense crowd. When that happens, the app issues a notification to remind the user to wear a mask.

## How we built it

We programmed the native app in Java using Android Studio. We used React and the React Google Maps API for the heatmap web app. We used Firebase Firestore to store location data. Fast and efficient nearby user lookup is accomplished via variable-precision geohashing. This allows us to quickly and accurately find users near a location at a specified precision.

## Challenges we ran into

Most of our team members were new to React, so we initially had difficulties learning how to integrate the Google Maps API in React. Additionally, a central component of this project is Firebase, as it was used as a connection between the webapp and the Android native app, and since our team was inexperienced with using Firebase at all learning on the fly proved to be one of our biggest challenges.

## Accomplishments that we're proud of

We were able to have the functionality of the heatmap supported on both mobile and desktop and make the UI simple and intuitive. Additionally, we set up an efficient database structuring that allows for fast user lookups. This allowed us to only request and render data in the area being viewed by the webapp, decreasing bandwidth usage and improving responsiveness.

## What we learned

We learned a lot about how to integrate the Google Maps API into a React component and update the map’s component state to center around the user’s chosen location. We learned how to navigate user data permissions and ensure users are aware of what data is collected on them and how it is collected.

## What's next for DawgMaps

We plan to have the app compile statistics for places of interest so we can display trends for each place, indicating how busy each place usually is at any time. Additionally, we’d like to partner with the UW contact tracing app to allow location updates from a wider demographic on the UW campus. Finally, not fully relying on the native app would be ideal. We would like to find alternative methods of detecting crowds of people, possibly by pinging cell phones from cell towers.