import { Flex, ProgressBar, View } from "@adobe/react-spectrum";
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import Placeholder from "../../assets/images/placeholder.png";
import Dialog from "../../components/console/dialog";
import Swiper from "../../components/swiper";
import { setSwipperCards } from "../../redux/consoleData";

const Processing = () => {
  const [progress, setProgress] = useState(0);
  const navigate = useNavigate();
  const consoleData = useSelector((state) => state.consoleData.apiResponse);
  const apiTotalCount = useSelector((state) => state.processApis.totalCount);
  const apiResults = useSelector((state) => state.downloadURLs.downloadURLs);
  const apiCurrentCount = useSelector(
    (state) => state.processApis.currentCount
  );
  const isConsoleOpen = useSelector(
    (state) => state.consoleToggle.isConsoleOpen
  );

  const [tempCounter, setTempCounter] = useState(0);

  const dispatch = useDispatch();

  useEffect(() => {
    console.log("Dispatching processApis");
    // dispatch(process());
  }, [dispatch]);

  /*  useEffect(() => {
    const timer = setInterval(() => {
      setProgress((oldProgress) => {
        const calculatedProgress = Math.floor(
          (apiCurrentCount / apiTotalCount) * 100
        );
        const newProgress = isNaN(calculatedProgress) ? 0 : calculatedProgress;
        if (newProgress === 100) {
          clearInterval(timer);
          setTimeout(() => {
            navigate("/result");
          }, 1000);
          return 100;
        }
        return Math.min(newProgress, 100);
      });
    }, 1000);

    return () => {
      clearInterval(timer);
    };
  }, [history, apiCurrentCount, apiTotalCount]);
 */
  useEffect(() => {
    if (apiResults.length > 0) {
      // Add a 3-second delay before navigating
      setTimeout(() => {
        navigate("/result");
      }, 5000);
    }
  }, [history, apiResults]);

  const imgData = useSelector((state) => state.consoleData.imgSrc);
  const swipperCards = useSelector((state) => state.consoleData.swipperCards);
  //const actionType = useSelector((state) => state.processApis.apiAudit);
  const eventLogs = useSelector((state) => state.logEvent.logEvents);

  useEffect(() => {
    let placeHolderData = { cover: Placeholder };
    let tempImgData = imgData;

    // Fill in the swipperCards with offset for how many api will be perfromed
    // Currently only using 1 offset at the moment
    imgData.map((item) => {
      let actualData = { cover: item.url };
      dispatch(setSwipperCards(actualData));
      dispatch(setSwipperCards(placeHolderData));
    });

    // Fill in the rest of the swipperCards with placeholder data since min is 5
    if (imgData.length < 2) {
      for (let i = 0; i < 5 - imgData.length; i++) {
        dispatch(setSwipperCards(placeHolderData));
      }
    }
  }, []);

  return (
    <Flex
      direction="column"
      justifyContent="center"
      alignItems="center"
      height="100%"
    >
      <Flex
        alignItems="center"
        height="100%"
        width="100%"
        justifyContent={isConsoleOpen ? "space-around" : "center"}
      >
        <Flex width={isConsoleOpen ? "70%" : "100%"}>
          <View width="100%">
            <Flex direction="column" alignItems="center">
              <Swiper nextTrigger={tempCounter} />
              <h1>
                {eventLogs[eventLogs.length - 1]?.Action ? (
                  <>{eventLogs[eventLogs.length - 1].Action}</>
                ) : (
                  "Process Starting..."
                )}
              </h1>
              <ProgressBar
                label="Processing..."
                //value={progress}
                width="size-2000"
                isIndeterminate
              />
            </Flex>
          </View>
        </Flex>
        <Flex width={isConsoleOpen ? "30%" : "0%"}>
          <View width="100%">
            <Dialog />
          </View>
        </Flex>
      </Flex>
    </Flex>
  );
};

export default Processing;
