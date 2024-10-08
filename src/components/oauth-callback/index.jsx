import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';  // Import Redux's dispatch
//import { login } from '../../services/authService';  // Import login action
import { login } from '../../redux/authReducer'; 

const Callback = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();  

  useEffect(() => {
    // Extract the authorization code from the URL
    const urlParams = new URLSearchParams(window.location.search);
    const authCode = urlParams.get('code');

    if (authCode) {
      console.log('authCode---------',authCode);
      fetchToken(authCode);
    } else {
      // Handle error or redirect to login if no code is present
      navigate('/login');
    }
  }, [navigate]);

  const fetchToken = async (authCode) => {
    try {
      const response = await fetch('/api/exchange-token', { // Update the URL to your server
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ authCode }), // Send auth code in the body
      });
      const data = await response.json();
      if (data.authData.access_token) {
        dispatch(login({ user: data.userData }));
        //const data = await response.json();
       // Redirect to the desired page after login
        navigate('/');
      } else {
        // Handle token retrieval failure
        navigate('/login');
      }
    } catch (error) {
      console.error('Token exchange failed', error);
      navigate('/login');
    }
  };

  return <div>Loading...</div>;
};

export default Callback;
