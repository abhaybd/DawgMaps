"use strict";

let map;
let marker;
let uw = {lat: 47.655548, lng: -122.303200};

function initMap() {
  map = new google.maps.Map(document.getElementById("map"), {
    center: uw,
    zoom: 14
  });
  marker = new google.maps.Marker({position: uw, map: map});
}