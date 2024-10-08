import React, { useEffect } from "react";
import { login } from "../../services/authService";
import { Button, Flex, Heading, Image, Text, View  } from "@adobe/react-spectrum";
import { Card } from "@react-spectrum/card";
import { Content } from "@react-spectrum/view";
import backgroundImage from "./login-background.png";
const Login = () => {
    const CLIENT_ID = 'CBJCHBCAABAApbsD-b_4RQuKSTGgI-sRNf8QWB673KWB';
    const REDIRECT_URI = 'https://localhost:8443/callback'; // Redirect URL after OAuth
    const SCOPE = 'user_login:account+agreement_write:account+user_read:account+user_write:account+agreement_read:account+	agreement_send:account+widget_read:account+widget_write:account+library_read:account+library_write:account+	workflow_read:account+workflow_write:account+webhook_read:account+	webhook_write:account+	application_read:account+	application_write:account';
    const AUTH_URL = `https://abhishekdixitg.in1.adobesign.com/public/oauth/v2?response_type=code&client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&scope=${SCOPE}`;
    
    const handleLogin = () => {
      // Redirect the user to the OAuth provider's authentication URL
      window.location.href = AUTH_URL;
    };
  
    return (
        <div
        style={{
          height: "100vh",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          background: `url(${backgroundImage}) no-repeat center center fixed`,
          backgroundSize: "cover",
          padding: "20px",
          position: "relative"
        }}
      >
        <View
        backgroundColor="white"
        padding="size-400"
        borderRadius="medium"
        boxShadow="0px 4px 16px rgba(0, 0, 0, 0.2)"
        width="300px"
        UNSAFE_style={{
          textAlign: "center",
          padding: "2rem"
        }}
      >
        <Card>
            <Content>
                        <Button
                           UNSAFE_style={{
                            width: "100%",
                            marginBottom: "1rem",
                            backgroundColor: "#0070d2",
                            color: "white"
                          }}
                            variant="accent"
                            style="fill"
                            onPress={handleLogin}
                        >
                            <Text>Login with Adobe Sign</Text>
                        </Button>
        </Content>
      </Card>
      </View>
      </div>
    );
  };
  
  export default Login;
