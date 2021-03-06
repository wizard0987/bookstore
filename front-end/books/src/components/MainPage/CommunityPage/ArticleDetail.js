import axios from "axios";
import { useState } from "react";
import { Link, useHistory } from "react-router-dom"

import jwtDecode from "jwt-decode"

import { Modal } from "react-bootstrap"
import { Button } from "@material-ui/core";
import styled from "styled-components";

const ArticleDetail = ({ article, token, email }) => {
  const [isShow, setIsShow] = useState(false);
  const history = useHistory();
  const { category, content, createdDate, title, id, memberEmail } = article
  
  const showOff = () => setIsShow(false);
  const showOn = () => setIsShow(true);

  const onClickEvent = () => {
    const { sub } = jwtDecode(token);
    axios.delete("api/board/delete", {
      data: {
        id,
        memberEmail:sub,
      }, headers: {
        Authorization:token
      }
    })
      .then(() => history.replace('/community'))
      .catch(err => console.log(err.response))
  }

  const DeleteCheckModal = () => {
    return <Modal show={isShow} onHide={showOff}>
      <Modal.Header closeButton>
        <Modal.Title>정말로 삭제하시겠습니까?</Modal.Title>
      </Modal.Header>
      <Modal.Body>글을 삭제하시면 내용과 함께 작성한 댓글까지 삭제됩니다.</Modal.Body>
      <Modal.Footer>
        <Button variant="contained" onClick={showOn}>
          취소</Button>
        <Button variant="contained" color="secondary" onClick={onClickEvent}>
          삭제</Button>
      </Modal.Footer>
    </Modal>
  }

  return <ArticleContainer>
    <ArticleTag>{category}</ArticleTag>
    <ArticleTitle>{title}</ArticleTitle>
    <hr />
    <ArticleContent>{content}</ArticleContent>
    <ArticleDate>{createdDate}</ArticleDate>
    {token !== undefined && memberEmail === email && <ButtonFeature>
      <EditButton
        component={Link}
        to={{pathname:"/community/update", state:{ data:{id, category, content, title}, token}}}
        variant="outlined"
        fontcolor="#ef9a9a"
        >
        수정
      </EditButton>
      
      <EditButton 
        variant="outlined"
        fontcolor="#ec407a"
        onClick={showOn}
        >
        삭제
      </EditButton>

    </ButtonFeature>}
    {isShow && <DeleteCheckModal />}

  </ArticleContainer>
}
export default ArticleDetail;

const ArticleContainer = styled.div`
  border: 1px solid #DFE8F2;
  border-radius: 20px;
  padding: 20px;
`
const ArticleTitle = styled.h2`
  margin: 10px 0 0 10px;
  font-size: 33px;
  font-weight: bold;
`
const ArticleContent = styled.div`
  padding: 20px;
  font-size: 18px;
  height: 300px;
`
const ArticleDate = styled.div`
  text-align: right;
  font-size: 14px;
  color:#A1A2AD;

  margin: 20px 0 0px 0;
`
const ArticleTag = styled.div`
  color: gray;
`
const ButtonFeature = styled.div`
  display: flex;
  justify-content: flex-end;

  margin-top: 30px;
`
const EditButton = styled(Button)`
  color: ${props => props.fontcolor};
  font-size: 20px;
  margin: 0 10px;

  &:hover{
    background-color: ${props => props.fontcolor};
    color: white;
  }
`