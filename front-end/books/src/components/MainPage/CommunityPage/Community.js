import axios from "axios"
import { Link } from "react-router-dom"
import { useState, useEffect } from "react"
import { useCookies } from "react-cookie";

import { Table } from "react-bootstrap"
import { Button } from "@material-ui/core"
import styled from "styled-components"

const Community = ({ history }) => {
  const [cookies] = useCookies(['token'])
  const token = cookies.token;
  const [articles, setArticles] = useState([]);
  const section = ["글 번호", "분류", "제목", "작성시간", "댓글 수"]

  useEffect(() => {
    getArticles();
  }, [])

  const getArticles = async () => {
    const {data: { dtoList }} = await axios.get("api/board");
    setArticles(dtoList)
  }

  const articleDetailEvent = (id) => {
    history.push(`/community/detail/${id}`)
  }

  return <div>
    {articles.length ? <Container hover responsive >
        <thead>
          <tr>{section.map((res, idx) => {
            return <Td key={idx}>{res}</Td>
          })}</tr>
        </thead>

        <tbody>
          {articles.map((article, idx) => {
            return <tr key={idx} onClick={() => articleDetailEvent(article.id)}>  
              <Td>{article.id}</Td>
              <Td>{article.category}</Td>
              <Td>{article.title}</Td>
              <Td>{article.modifiedDate}</Td>
              <Td>{article.replyCount}</Td>
            </tr>
          })}
        </tbody>
      </Container>
      : 
      <NoArticle>🙅‍♂️ 게시글이 없습니다</NoArticle>
      }

    {token !== undefined && <div style={{display:"flex", justifyContent:"flex-end"}}>
        <PostButton
          component={Link}
          to="/community/register"
          variant="contained"
          color="primary"
          >
          게시글 등록
        </PostButton>
      </div>
    }
  </div>
}
export default Community;


const Container = styled(Table)`
  margin: 0 auto;
  width: 80%;

  td {
    text-align: center;
    vertical-align: middle; 
    font-weight: 600;
    height: 100px;
  } 
`
const NoArticle = styled.div`
  margin: 50px 0 0 10%;
  font-size: 24px;
  font-weight: bold;
`

const PostButton = styled(Button)`
  margin: 20px 10%;
  font-size: 18px;

  &:hover {
    background-color: white;
    color: #283593;
  }
`
const Td = styled.td`
  font-size: 1.3vw;
`