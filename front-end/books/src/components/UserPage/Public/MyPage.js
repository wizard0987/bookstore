import UserInfo from "./UserInfo";
import OrderList from "./OrderComponent/OrderList";
import MyPost from "./MyPost";
import axios from "axios";
import { useState } from "react";
import { useHistory, Route, Switch, NavLink } from "react-router-dom";
import { useCookies } from "react-cookie";
import Sticky from "react-sticky-el"

import PanToolTwoToneIcon from '@material-ui/icons/PanToolTwoTone';
import { MenuList, MenuItem, Paper,TextField, Button } from "@material-ui/core"
import { Modal } from "react-bootstrap";
import styled, { keyframes } from "styled-components";

const MyPage = ({ match }) => {
  const history = useHistory();
  const [cookies, setCookie, removeCookie] = useCookies(["token"]);
  const token = cookies.token;
  const [show, setShow] = useState(false);
  const [password, setPassword] = useState("");

  const modalOpen = () => setShow(true);
  const modalClose = () => setShow(false);

  const deleteAccount = (e) => {
    modalClose();
    axios.post("api/withdrawal",
      { password },
      { headers: { Authorization: token } }
    )
    .then(() => {
      removeCookie("token");
      history.replace("/");
    })
    .catch((err) => console.log(err.response));
  };

  const { path } = match;

  return (
		<Container>
			<MenuListStyled>
        <Sticky>
          <StyledPaper elevation={2}>
            <MenuItemStyled component={NavLink} to={{ pathname: `${path}/userinfo`, state: { token } }}>
              회원정보
            </MenuItemStyled>
            <MenuItemStyled component={NavLink} to={{ pathname: `${path}/orderlist`, state: { token } }}>
              구매내역
            </MenuItemStyled>
            <MenuItemStyled component={NavLink} to={{ pathname: `${path}/mypost`, state: { token } }}>
              쓴글보기
            </MenuItemStyled>
            <MenuItemStyled onClick={modalOpen}>
              회원탈퇴
            </MenuItemStyled>
          </StyledPaper>
        </Sticky>
			</MenuListStyled>
      
      <Switch>
				<Route path={`${path}/userinfo`} component={UserInfo} />
				<Route path={`${path}/orderlist`} component={OrderList} />
				<Route path={`${path}/mypost`} component={MyPost} />
			</Switch>

			<Modal show={show} onHide={modalClose} size="lg">
				<Modal.Header closeButton>
          
					<Modal.Title style={{fontSize:"30px"}}>
            <StyledPanToolTwoToneIcon color="secondary" fontSize="large" alt="멈춰!" /> 정말로 탈퇴하겠습니까?
          </Modal.Title>
				</Modal.Header>
				<Modal.Body>
					<Title>
            탈퇴시 개인정보와 함께 모든 이용내역이 지워집니다.<br />
					  마지막으로 <Title2>회원님의 비밀번호</Title2> 입력하시고 회원 탈퇴를 눌러주세요.
          </Title>
          <TextField 
            type="password"
            label="Your Password!!" 
            variant="outlined" 
            required
            onChange={(e) => setPassword(e.target.value)} 
            />
					
				</Modal.Body>
				<Modal.Footer>
					<Button color="secondary" size="large" onClick={deleteAccount}>
						회원탈퇴
					</Button>
					<Button size="large" onClick={modalClose}>
						취소
					</Button>
				</Modal.Footer>
			</Modal>
		</Container>
	);
};
export default MyPage;

const Container = styled.div`
	display: inline-flex;
`;

const MenuListStyled = styled(MenuList)`  
	width: 13vw;
  `
const MenuItemStyled = styled(MenuItem)`
  font-family: 'Jua', sans-serif;
  font-size: 20px;
  font-weight: 500;
  background-color: #ffecb3;

  margin: 5px 0;
`
const StyledPaper = styled(Paper)`

  display: flex;
  flex-direction: column;
  align-items: center;
	
  height: auto;
  border: 1px solid white;
  background-color: #ffecb3;
`
const SwitchDiv = styled.div`
	margin-left: 2vw;
`
const animate = keyframes`
  0% {
    transform: scale(1.0);
  }
  50%{
    transform: scale(1.5);
  }
  100% {
    transform: scale(1.0);
  }

`
const StyledPanToolTwoToneIcon = styled(PanToolTwoToneIcon)`
  animation: ${animate} 2s linear infinite; 
`

const Title = styled.div`
  margin: 30px 10px;
  font-size: 20px;
`
const Title2 = styled.span`
  font-size: 22px;
  font-weight: bold;
`