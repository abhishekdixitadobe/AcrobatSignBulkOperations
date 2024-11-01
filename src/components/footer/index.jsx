import { Button, ButtonGroup, Flex, View } from "@adobe/react-spectrum";
import Console from "../../components/console";
import React from "react";
import { useLocation } from "react-router-dom";
import UploadPageButton from "../../components/console/uploadPageButton";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

const Footer = (props) => {
  const {
    disabledBack = false,
    disabledExecute = false,
    executeOnPress = () => {},
    showDownload = false,
    showDownloadFormField =false,
    downloadOnPress = () => {},
    downloadFormField = () => {},
    configs = null,
    heading = "",
  } = props;

  const location = useLocation();
  const isDisabled = useSelector((state) => state.consoleToggle.isDisabled);

  const navigate = useNavigate();
  const handleNavigation = () => {
    navigate(configs.page, { state: { heading, configs } });
  };

  return (
    <View backgroundColor={"gray-50"} height="100%">
      <Flex direction="row" height="100%" gap="size-100" alignItems={"center"}>
        <View paddingX={"size-800"} width="100%">
          <Flex justifyContent="space-between">
            <Flex justifyContent="end">
              <ButtonGroup>
                <Button
                  variant="secondary"
                  onPress={() => {
                    window.history.back();
                  }}
                  isDisabled={disabledBack}
                >
                  Back
                </Button>

                {!configs ? (
                  <>
                    {showDownload ? (
                      <Button variant="cta" onPress={downloadOnPress}>
                        Download
                      </Button>
                    ) : ''}
                  </>
                ) : ''}
          
                {showDownloadFormField ? (
                  <Button variant="cta" onPress={downloadFormField}>
                    Download Form Fields
                  </Button>
                ): ''}
              </ButtonGroup>
            </Flex>
          </Flex>
        </View>
      </Flex>
    </View>
  );
};

export default Footer;
