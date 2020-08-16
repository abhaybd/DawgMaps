import React from 'react';
import {Link} from "react-router-dom";
import {Nav, Navbar} from 'react-bootstrap';
import styled from 'styled-components';
import Logo from "./maphusky2.png";


const Styles = styled.div`
    .navbar {background-color: #4b2e83;}
    a, .navbar-nav, .navbar-light .nav-link {
    color: #b7a57a;
    // margin-right: 5px;
    &:hover { color: white; }
    &:focus { color: #b7a57a; }
    &:active{ color: #b7a57a; }
  }
  .navbar-brand {
    font-size: 1.4em;
    color: #b7a57a;
    &:hover { color: white; }
    &:focus { color: #b7a57a; }
    &:active{ color: #b7a57a; }
  }
  .form-center {
    position: absolute !important;
    left: 25%;
    right: 25%;
  }
  
`;

export const NavigationBar = () => (
    <Styles>
        <Navbar expand="lg">
            <Link className="navbar-brand" to="/">
                <b>DAWG MAPS</b>
            </Link>
            <Link className="navbar-brand" to="/">
                <img
                    alt=""
                    src={Logo}
                    width="30"
                    height="30"
                    className="d-inline-block align-top"
                />
            </Link>
            <Navbar.Toggle aria-controls="basic-navbar-nav"/>
            <Navbar.Collapse id="basic-navbar-nav">
                <Nav className="ml-auto">
                    <Nav.Item><Link className="nav-link" to="/"><b>Home</b></Link></Nav.Item>
                    <Nav.Item><Link className="nav-link" to="/about"><b>About</b></Link></Nav.Item>
                </Nav>
            </Navbar.Collapse>
        </Navbar>
    </Styles>
)