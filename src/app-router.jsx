/*************************************************************************
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 * Copyright 2024 Adobe
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of Adobe and its suppliers, if any. The intellectual
 * and technical concepts contained herein are proprietary to Adobe
 * and its suppliers and are protected by all applicable intellectual
 * property laws, including trade secret and copyright laws.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe.
 **************************************************************************/

import React, { useState } from "react";
import { Route, Routes } from "react-router-dom";
import Landing from "./views/landing";
import Result from "./views/result";
import SocialPost from "./views/social-post";
import SetupView from "./views/social-post/setup";
import Upload from "./views/upload";
import Info from "./views/info";
import Login from "./views/login";
import OAuthCallback from "./components/oauth-callback"; // Handle OAuth callback
import ProtectedRoute from "./components/protected-route"; // Import ProtectedRoute component
import AgreementsPage from "./views/agreements";

const AppRouter = () => {
  const [context, setContext] = useState(null);

  const handleContextChange = (payload) => {
    setContext(payload);
  };

  const basename = window.location.pathname.substr(
    0,
    window.location.pathname.lastIndexOf("/")
  );

  return (
    <Routes>
      {/* Unprotected route for Login */}
      <Route path={`${basename}/login`} element={<Login />} />

      {/* OAuth callback route */}
      <Route path={`${basename}/callback`} element={<OAuthCallback />} />

      {/* Protected routes */}
      <Route
        path={`${basename}/`}
        element={
          <ProtectedRoute>
            <Landing />
          </ProtectedRoute>
        }
      />
      <Route
        path={`${basename}/agreementsList/`}
        element={
          <ProtectedRoute>
            <AgreementsPage onContextChange={(context) => handleContextChange(context)} />
          </ProtectedRoute>
        }
      />
      <Route
        path={`${basename}/social-post/`}
        element={
          <ProtectedRoute>
            <SocialPost onContextChange={(context) => handleContextChange(context)} />
          </ProtectedRoute>
        }
      />
      <Route
        path={`${basename}/social-post/setup/`}
        element={
          <ProtectedRoute>
            <SetupView context={context} />
          </ProtectedRoute>
        }
      />
      <Route
        path={`${basename}/upload/`}
        element={
          <ProtectedRoute>
            <Upload />
          </ProtectedRoute>
        }
      />
      <Route
        path={`${basename}/result/`}
        element={
          <ProtectedRoute>
            <Result />
          </ProtectedRoute>
        }
      />
      <Route
        path={`${basename}/info/`}
        element={
          <ProtectedRoute>
            <Info />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
};

export default AppRouter;
