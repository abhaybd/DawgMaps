import React from 'react';
import styled from 'styled-components';

const GridWrapper = styled.div`
  display: inline-block;
  grid-gap: 10px;
  margin-top: 1em;
  margin-left: 1em;
  margin-right: 1em;
  grid-template-columns: repeat(12, 1fr);
  grid-auto-rows: minmax(25px, auto);
`;

export const About = () => (
  <GridWrapper>
    <p>In response to the COVID-19 pandemic, DawgMaps provides users with realtime information about
large gatherings and crowds in their area to help users intelligently social distance.  
We designed a native mobile app to crowdsource location data, compiled into a cloud database, and generated population density heatmaps on this online platform.
Our team consists of 5 UW students, and we developed this project for the Hack'20 Hackathon.</p>
  </GridWrapper>
)