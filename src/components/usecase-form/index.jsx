import React from "react";
import BulkOpForm from "./bulkOpForm";
import DropZoneForm from "./dropZoneForm";
import BannerDropZoneForm from "./bannerDropZoneForm";
import AgreementForm from "./agreementForm"

const UseCaseForm = ({ id, onFormChange, setUploadFiles }) => {
  switch (id) {
    case "BulkOpForm":
      return (
        <BulkOpForm onChange={onFormChange} setUploadFiles={setUploadFiles} />
      );
    case "agreements":
      return (
        <AgreementForm
          action="agreements"
          onChange={onFormChange}
          setUploadFiles={setUploadFiles}
        />
      );
    case "BrandedContentGeneration":
      return (
        <BannerDropZoneForm
          action="BrandedContentGeneration"
          onChange={onFormChange}
          setUploadFiles={setUploadFiles}
        />
      );
    case "ProductPlacement":
      return (
        <DropZoneForm
          action="ProductPlacement"
          onChange={onFormChange}
          showCSVDropZone
        />
      );
    default:
      return <>Nothing to be display</>;
  }
};

export default UseCaseForm;
