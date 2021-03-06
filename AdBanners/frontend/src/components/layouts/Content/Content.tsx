import * as React from "react";
import { FC } from "react";
import { ActiveTab } from "../../../ActiveTab";
import { ContentEmpty } from "./ContentEmpty";
import { ContentBannerForm } from "./ContentBannerForm";
import { ContentHeader } from "./ContentHeader";
import { ContentCategoryForm } from "./ContentCategoryForm";
import { CategoryType } from "../../models/Categories";
import { BannerType } from "../../models/Banners";

interface Props {
  element: BannerType & CategoryType;
  type: ActiveTab;
  categories: CategoryType[];
}

export const Content: FC<Props> = ({ element, type, categories }) => {
  //If no element is selected - render empty view.
  if (element === null) return <ContentEmpty />;

  //If currently selected tab is Banners - display form for banners.
  //Else - display form for categories.
  if (type === ActiveTab.Banners) return bannerContent();
  return categoryContent();

  function bannerContent() {
    return (
      <section className="content">
        <ContentHeader
          elementId={element.id}
          elementName={element.name}
          activeTab={type}
        />

        <ContentBannerForm element={element} categories={categories} />
      </section>
    );
  }

  function categoryContent() {
    return (
      <section className="content">
        <ContentHeader
          elementId={element.id}
          elementName={element.name}
          activeTab={type}
        />
        <ContentCategoryForm element={element} />
      </section>
    );
  }
};
