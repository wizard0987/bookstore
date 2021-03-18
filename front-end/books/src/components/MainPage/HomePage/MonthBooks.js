import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faHandPointDown } from '@fortawesome/free-solid-svg-icons'
import NextArrow from "./CustomArrow/NextArrow"
import PrevArrow from "./CustomArrow/PrevArrow"
import { useState, useEffect } from "react"
import { useHistory } from "react-router-dom"
import styled from "styled-components"
import axios from "axios"
import Slider from "react-slick";

const MonthBooks = () => {
  const [images, setImages] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const histroy = useHistory();

  useEffect(() => {
    const getImage = async () => {
      await axios.get("http://localhost:8080/api/index/thismonth/")
        .then(res => setImages(res.data))
        .catch(err => console.log(err))
    }
    getImage()
    return setIsLoading(true);
  }, [])

  const clickEvent = (book) => {
    histroy.push({
      pathname: "/detail",
      search: `?id=${book.id}`,
      state: book
    })
  }

  const settings = {
    arrows: true,
    dots: true,
    infinite: true,
    speed: 800,
    slidesToShow: 4,
    slidesToScroll: 3,
    nextArrow: <NextArrow />,
    prevArrow: <PrevArrow />
  };

  return <Container>
    <ContainerTitle>이달의 Books!</ContainerTitle>
    <FontAwesomeIcon icon={faHandPointDown} style={{ fontSize: "60px", color: "#74ABE3" }} />

    { isLoading ? 
      <Slider {...settings}>
        {images && images.map((res, idx) => {
          return <EachBook key={idx}>
            <BookButton onClick={() => clickEvent(res)}>
              <img src={res.imageUrl} alt={idx} />
            </BookButton>
          </EachBook>
        })}
      </Slider>
    :
    <h1>Loading....</h1>
    }
  </Container>
}
export default MonthBooks;


const Container = styled.div`
  position: relative;
  margin: 0 auto;
  width: 90%;
`

const ContainerTitle = styled.span`
  font-size: 2.5rem;
  font-weight:900;
  position:relative;
  top: -10px;
  margin-right:20px;
`

const EachBook = styled.div`
  position: relative;
  width: auto;
  max-height: 400px;
  overflow: hidden;
  object-fit: cover;
  border-radius: 5px;
  
`

const BookButton = styled.button`
  border: 0 none;
  background-color: transparent;

  width:220px;
  height:280px;
  
`