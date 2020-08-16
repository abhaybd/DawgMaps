import React from "react";
import {GoogleMap, HeatmapLayer, LoadScript, Marker, StandaloneSearchBox} from '@react-google-maps/api';
import Geocode from "react-geocode";
import {db} from "./Database";

const containerStyle = {
    position: 'fixed',
    overflowY:'hidden',
    marginTop:'5px',
    top:'inherit',
    width: '100%',
    height: '85vh'
};

const libraries = ["visualization", "places"];

const mapOptions = {
    disableDefaultUI: true
}

Geocode.setApiKey(process.env.REACT_APP_API_KEY);

function renderHeatmap(data, LatLng, bounds) {
    // Za = latitude, Va = longitude
    db.getUsersInArea(bounds.Za.i, bounds.Za.j, bounds.Va.i, bounds.Va.j, function (arr) {
        console.log(`Found ${arr.length} users!`);
        data.clear();
        for (let coord of arr) {
            data.push(new LatLng(coord.lat, coord.lng));
        }
    });
}

export default function Map(props) {
    const [map, setMap] = React.useState(null);
    const [searchBox, setSearchBox] = React.useState(null);
    const [center, setCenterPos] = React.useState(props.center);
    const [heatmap, setHeatmap] = React.useState(null);
    const [started, setStarted] = React.useState(false);

    if (!started) {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                let loc = {lat: position.coords.latitude, lng: position.coords.longitude};
                console.log(loc);
                setCenterPos(loc);
                setStarted(true);
            });
        }
    }

    const onLoad = React.useCallback(function callback(map) {
        setMap(map);
    }, []);

    const onUnmount = React.useCallback(function callback(map) {
        setMap(null);
    }, []);

    const heatmapLoad = layer => {
        setHeatmap(layer);
    }

    const onIdle = () => {
        if (heatmap.data) {
            renderHeatmap(heatmap.data, window.google.maps.LatLng, map.getBounds());
        }
    }

    const searchBoxLoad = ref => setSearchBox(ref);
    const onPlacesChanged = () => Geocode.fromAddress(searchBox.getPlaces()[0]['formatted_address']).then(
        response => {
            let position = response.results[0].geometry.location;
            console.log(position);
            if (map) {
                map.setCenter(position);
            }
            setCenterPos(position);
        },
        error => {
            console.error(error);
        }
    );

    return (
        <LoadScript
            googleMapsApiKey={process.env.REACT_APP_API_KEY}
            libraries={libraries}
        >
            <GoogleMap
                mapContainerStyle={containerStyle}
                center={center}
                zoom={props.zoom ?? 14}
                onLoad={onLoad}
                onUnmount={onUnmount}
                onIdle={onIdle}
                options={mapOptions}
            >
                <StandaloneSearchBox
                    onLoad={searchBoxLoad}
                    onPlacesChanged={onPlacesChanged}
                >
                    <input
                        type="text"
                        placeholder="Search"
                        style={{
                            boxSizing: `border-box`,
                            border: `1px solid transparent`,
                            width: `240px`,
                            height: `32px`,
                            padding: `0 12px`,
                            borderRadius: `3px`,
                            boxShadow: `0 2px 6px rgba(0, 0, 0, 0.3)`,
                            fontSize: `14px`,
                            outline: `none`,
                            textOverflow: `ellipses`,
                            position: "absolute",
                            left: "50%",
                            marginLeft: "-120px",
                            marginTop: "10px"
                        }}
                    />
                </StandaloneSearchBox>
                <Marker
                    position={center}
                />
                <HeatmapLayer data={[]} onLoad={heatmapLoad}/>
                { /* Child components, such as markers, info windows, etc. */}
            </GoogleMap>
        </LoadScript>
    )
}