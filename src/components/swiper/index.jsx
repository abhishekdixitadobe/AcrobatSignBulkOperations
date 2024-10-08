import React, { useEffect, useState, useRef } from "react";
import { View, Button } from "@adobe/react-spectrum";
import {
  StackedCarousel,
  ResponsiveContainer,
} from "react-stacked-center-carousel";
import { useSelector } from "react-redux";
import { useDispatch } from "react-redux";
import { changeSwipperCardImage } from "../../redux/consoleData";
import Card1 from "../../assets/images/A-1.png";
import Card2 from "../../assets/images/A-2.png";
import Card3 from "../../assets/images/A-3.png";
import Card4 from "../../assets/images/A-4.png";

const SwiperUI = (props) => {
  const { nextTrigger } = props;
  const ref = useRef(null);

  // const swipperCards = useSelector((state) => state.consoleData.swipperCards);
  const swipperCards = [
    { cover: Card1 },
    { cover: Card2 },
    { cover: Card3 },
    { cover: Card4 },
  ];

  const apiResults = useSelector((state) => state.downloadURLs.downloadURLs);
  const [offsetCounter, setOffsetCounter] = useState(1);
  const dispatch = useDispatch();

  useEffect(() => {
    let latestStreamURL = apiResults[apiResults.length - 1];
    if (latestStreamURL) {
      let coverData = { cover: latestStreamURL };
      dispatch(
        changeSwipperCardImage({
          index: offsetCounter,
          cover: coverData,
        })
      );
      setOffsetCounter((prevCounter) => prevCounter + 2);
    }
  }, [apiResults]);

  // useEffect(() => {
  //   if (nextTrigger) {
  //     ref.current.goNext();
  //   }
  // }, [nextTrigger]);

  useEffect(() => {
    const timer = setInterval(() => {
      if (ref.current) {
        ref.current.goNext();
      }
    }, 2000);
  }, []);

  return (
    <View width="100%">
      <div style={{ width: "100%", position: "relative" }}>
        <ResponsiveContainer
          carouselRef={ref}
          render={(parentWidth, carouselRef) => {
            let currentVisibleSlide = 5;
            if (parentWidth >= 1920) currentVisibleSlide = 5;
            // if (parentWidth <= 1920) currentVisibleSlide = 3;
            return (
              <StackedCarousel
                ref={carouselRef}
                slideComponent={Card}
                slideWidth={parentWidth < 400 ? parentWidth - 40 : 350}
                carouselWidth={parentWidth}
                data={swipperCards}
                currentVisibleSlide={currentVisibleSlide}
                disableSwipe={true}
                maxVisibleSlide={5}
                useGrabCursor
                fadeDistance={0.1}
              />
            );
          }}
        />
      </div>
    </View>
  );
};

export default SwiperUI;

export const Card = React.memo(function (props) {
  const { data, dataIndex } = props;
  const { cover } = data[dataIndex];
  return (
    <div
      style={{
        width: "100%",
        height: 300,
        userSelect: "none",
      }}
      className="my-slide-component"
    >
      <img
        style={{
          height: "100%",
          width: "100%",
          objectFit: "cover",
          borderRadius: 0,
        }}
        draggable={false}
        src={cover}
      />
    </div>
  );
});
