import { Button, Flex, Text } from "@adobe/react-spectrum";
import React from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { setAPI, setLandingPage } from "../../redux/navState";

const AgreementAction = ({ params, onAction, buttonText = "Submit", isDisabled, configs, heading }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const handleButtonClick = () => {
    if (onAction) {
      onAction(params); // Trigger the passed action with provided parameters
      // navigate(configs.page, { state: { configs } });
      dispatch(setAPI(configs.api));
      dispatch(setLandingPage(false));
      //navigate("/info", { state: { heading: heading, configs: configs } });
      navigate(configs.page, { state: { heading, configs } });
    }
  };

  return (
        <Button
          UNSAFE_style={{ whiteSpace: "nowrap" }}
          variant="accent"
          style="fill"
          onPress={handleButtonClick}
          isDisabled={isDisabled}
          alignSelf="end"
        >
          <Text>{buttonText}</Text>
        </Button>
  );
};

export default AgreementAction;
