import React, { useState, useRef } from "react";
import { Flex, DatePicker, TextField, Button } from "@adobe/react-spectrum";
import AgreementAction from "../../components/agreement-action";
import { parseDate } from "@internationalized/date";
import { useSelector, useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";

const AgreementForm = () => {
  const [startDate, setStartDate] = useState(parseDate("2020-02-03"));
  const [endDate, setEndDate] = useState(parseDate("2020-02-03"));
  const [email, setEmail] = useState("");


  const authState = useSelector((state) => state.auth || {});
  const isAuthenticated = authState.isAuthenticated || false;
  const user = authState.user;
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const configs = {
    page: "/agreements",
    api: "/api/agreements",
  };

  const handleApiCall = async (params) => {
    const { startDate, endDate, email } = params;
    try {
      const apiUrl = `/api/search`;
      const reqBody = {
        startDate: startDate,
        endDate: endDate,
        email: email,
        selectedStatus: Array.from(selectedStatus),
      };
      const response = await fetch(apiUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer ",
        },
        body: JSON.stringify(reqBody),
      });

      if (response.ok) {
        const data = await response.json();
        console.log("Agreements Data: ", data);
      } else {
        console.error("API call failed", response.statusText);
      }
    } catch (error) {
      console.error("Error making API call:", error);
    }
  };


  return (
    <>
      <Flex gap="size-150" wrap>
        <DatePicker label="Start Date" value={startDate} onChange={setStartDate} />
        <DatePicker label="End Date" value={endDate} onChange={setEndDate} />
        <TextField
          label="User Email"
          value={email}
          onChange={setEmail}
          type="email"
        />
        <AgreementAction
          params={{ startDate, endDate, email }}
          onAction={handleApiCall}
          buttonText="Get Agreements"
          isDisabled={!email}
          configs={configs}
          heading="Agreements"
        />
      </Flex>
    </>
  );
};

export default AgreementForm;