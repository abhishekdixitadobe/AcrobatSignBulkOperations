import {
  Button,
  ButtonGroup,
  Content,
  Dialog,
  DialogTrigger,
  Divider,
  Heading,
  Provider,
  Text,
  Header,
  View,
  LabeledValue,
  Flex,
} from "@adobe/react-spectrum";
import React, { useEffect, useRef } from "react";
import ReactJson from "react-json-view";
import CloseIcon from "@spectrum-icons/workflow/Close";
import { useSelector } from "react-redux";
import { useDispatch } from "react-redux";
import { setIsConsoleOpen } from "../../redux/consoleToggle";

const Console = () => {
  // const imgSrc = useSelector((state) => state.consoleData.imgSrc);
  // const apiResponse = useSelector((state) => state.consoleData.apiResponse);

  const actionType = useSelector((state) => state.consoleData.bulkActionType);
  const imgSrc = useSelector((state) => state.processApis.results);
  const apiResponse = useSelector((state) => state.logEvent.logEvents);

  const isOpen = useSelector((state) => state.consoleToggle.isConsoleOpen);
  const dispatch = useDispatch();

  const handleCloseDialog = () => {
    dispatch(setIsConsoleOpen(false));
  };

  const scrollRef = useRef(null);
  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [apiResponse]);

  return (
    <Provider colorScheme="dark" width="100%">
      {isOpen && (
        <Dialog width="100%" isDismissable onDismiss={handleCloseDialog}>
          <Heading>Console Log</Heading>
          <Header>
            {/*  <ButtonGroup>
              <Button variant="accent" onPress={handleCloseDialog}>
                <CloseIcon />
                <Text>Close</Text>
              </Button>
            </ButtonGroup> */}
          </Header>
          <Divider />
          <Content>
            <div
              ref={scrollRef}
              style={{ height: "50vh", width: "100%", overflow: "auto" }}
            >
              <Content>
                {apiResponse.length > 0 ? (
                  apiResponse.map((item, index) => (
                    <View
                      key={index}
                    >
                      <Text
                        UNSAFE_style={{
                          fontSize: "14px",
                          fontWeight: "bold",
                          marginBottom: "10px",
                        }}
                      >
                        {actionType}
                      </Text>
                      <View
                        UNSAFE_style={{
                          display: "flex",
                          flexDirection: "column",
                          gap: "10px",
                        }}
                      >
                        {item.Action && (
                          <LabeledValue label="Action:" value={item.Action} />
                        )}
                        {item.Method && (
                          <LabeledValue label="Method:" value={item.Method} />
                        )}
                        {item.URL && (
                          <LabeledValue label="URL:" value={item.URL} />
                        )}
                        {item.Request && (
                          <>
                            <LabeledValue label="Request" value />
                            <ReactJson
                              src={item.Request}
                              theme={"bright"}
                              displayDataTypes={false}
                              displayObjectSize={false}
                              indentWidth={2}
                              style={{ fontFamily: "adobe-clean" }}
                              collapsed={true}
                            />
                          </>
                        )}
                        {item.Response && (
                          <>
                            <LabeledValue label="Response:" value="" />
                            <ReactJson
                              src={item.Response}
                              theme={"bright"}
                              displayDataTypes={false}
                              displayObjectSize={false}
                              indentWidth={2}
                              style={{ fontFamily: "adobe-clean" }}
                              collapsed={true}
                            />
                          </>
                        )}
                      </View>
                      <Divider size="M" UNSAFE_style={{ margin: "10px 0" }} />
                    </View>
                  ))
                ) : (
                  <Text>No API response</Text>
                )}
              </Content>
            </div>
          </Content>
        </Dialog>
      )}
    </Provider>
  );
};

export default Console;
