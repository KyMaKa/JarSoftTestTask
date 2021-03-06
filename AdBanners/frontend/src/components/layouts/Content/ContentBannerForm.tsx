import { AxiosError, AxiosResponse } from "axios";
import * as React from "react";
import { FC, useState } from "react";
import Select, { MultiValue, OnChangeValue } from "react-select";
import { BannerType } from "../../models/Banners";
import { CategoryType } from "../../models/categories";
import { BannerService } from "../../services/BannersService";
import { Error } from "../Validation/Error";
import { Success } from "../Validation/Success";
import { ContentFooter } from "./ContentFooter";

interface Props {
  element: BannerType;
  categories: CategoryType[];
}

export const ContentBannerForm: FC<Props> = ({ element, categories }) => {
  const [status, setStatus] = useState<number>(null);
  const [message, setMessage] = useState<string>("");
  const [name, setName] = useState<string>(element.name);
  const [price, setPrice] = useState<number>(element.price);
  const [text, setText] = useState<string>(element.text);
  const [bannerCategories, setbannerCategories] = useState<CategoryType[]>(
    element.categories
  );

  //Sets the element values on selecting element.
  React.useEffect(() => {
    setName(element.name);
    setPrice(element.price);
    setbannerCategories(element.categories);
    setText(element.text);
    setStatus(null);
    setMessage("");
  }, [element]);

  //Resets validation status.
  React.useEffect(() => {
    setStatus(null);
  }, [name]);

  return (
    <>
      <div className="content__body">
        <div className="content__form">
          <div className="content__form-item">
            <div className="content__form-item-title">Name</div>
            <div className="content__form-item-content">
              <input
                className="content__input"
                type="text"
                value={name}
                onChange={handleChangeName}
              />
            </div>
          </div>
          <div className="content__form-item">
            <div className="content__form-item-title">Price</div>
            <div className="content__form-item-content">
              <input
                className="content__input"
                type="number"
                value={price}
                onChange={handleChangePrice}
              />
            </div>
          </div>
          <div className="content__form-item">
            <div className="content__form-item-title">Category</div>
            <div className="content__form-item-content">
              <Select
                className="content__select"
                classNamePrefix="select"
                isMulti
                value={categories.map((category) => {
                  if (
                    bannerCategories.some((e) => {
                      return e.id === category.id;
                    })
                  )
                    return { value: category, label: category.name };
                })}
                options={categories.map((category) => {
                  return { value: category, label: category.name };
                })}
                onChange={handleChangeCategory}
              />
            </div>
          </div>
          <div className="content__form-item">
            <div className="content__form-item-title">Text</div>
            <div className="content__form-item-content">
              <textarea
                className="content__textarea"
                value={text}
                onChange={handleChangeText}
              ></textarea>
            </div>
          </div>
        </div>
      </div>
      <ContentFooter
        updateItem={element.id === 0 ? addBanner : updateBanner}
        deleteItem={deleteBanner}
      />
      {status >= 400 ? <Error message={message} /> : null}
      {status != null && status < 300 ? <Success message={message} /> : null}
    </>
  );

  function handleChangeName(event: React.BaseSyntheticEvent) {
    const value = event.target.value;
    setName(value);
  }
  function handleChangePrice(event: React.BaseSyntheticEvent) {
    const value = event.target.value;
    setPrice(value);
  }

  function handleChangeCategory(
    newValue: MultiValue<{ label?: string; value?: CategoryType }>
  ) {
    let newCategories: CategoryType[] = [];
    newValue.map((item) => {
      newCategories.push(item.value);
    });
    setbannerCategories(newCategories);
  }

  function handleChangeText(event: React.BaseSyntheticEvent) {
    const value = event.target.value;
    setText(value);
  }

  //Set request on backend to update existing banner.
  function updateBanner() {
    let banner: BannerType = element;
    // banner.id = element.id;
    banner.name = name;
    banner.price = price;
    banner.categories = bannerCategories;
    banner.text = text;
    banner.deleted = false;
    BannerService.updateBanner(banner.id, banner)
      .then((response: AxiosResponse) => {
        setMessage("Banner updated.");
        console.log(response.status);
        setStatus(response.status);
      })
      .catch((error: AxiosError) => {
        setMessage(error.response.data.detail);
        setStatus(error.response.status);
      });
  }

  function deleteBanner() {
    BannerService.deleteBanner(element.id)
      .then((response: AxiosResponse) => {
        setMessage("Banner deleted.");
        setStatus(response.status);
      })
      .catch((error: AxiosError) => {
        setMessage(error.response.data.detail);
        setStatus(error.response.status);
      });
  }

  //In this case new empty entity is already created
  //So we just set it's values.
  function addBanner() {
    element.name = name;
    element.price = price;
    element.categories = bannerCategories;
    element.text = text;
    BannerService.postBanner(element)
      .then((response: AxiosResponse) => {
        setMessage("Banner added.");
        setStatus(response.status);
      })
      .catch((error: AxiosError) => {
        setMessage(error.response.data.detail);
        setStatus(error.response.status);
      });
  }
};
