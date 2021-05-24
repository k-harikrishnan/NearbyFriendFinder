<?php 
	include "./database_con.php";
	header('Content-type: application/json'); 
	
	if ($con->connect_error) {
			die("Connection failed: " . $con->connect_error);
		} 
		
	if (isset($_POST['tag']) && $_POST['tag'] != ''){
		$tag = $_POST['tag'];
		
		if ($tag == 'Register'){
			$phone=$_POST['phone'];
			$name=$_POST['name'];
			$email=$_POST['email'];
			$image = $_POST['image'];
			$queryid=" SELECT  id FROM `user_tbl` ORDER BY id ASC ";
			$resq=$con->query($queryid);
			if ($resq->num_rows>0) {
			while($row=$resq->fetch_assoc())	{		
					$id=$row['id'];
					
					}	
					
				}else{
				$id=0;
				}
			$imgpath="../Friendfinder/images/$id.png";
			$imgpath1="/Friendfinder/images/$id.png";
			
			$query ="SELECT  * FROM `user_tbl` WHERE `emailid`='".$email."' and `phone`='".$phone."'";
			$r=$con->query($query);
			if ($r->num_rows ==0) {
				$sql ="INSERT INTO `user_tbl`( `phone`, `name`,`emailid`,image,otpflag) VALUES ('".$phone."','".$name."','".$email."','".$imgpath1."','0')";
				$result = $con->query($sql);

				if ($result){
					// output data of each row
					file_put_contents($imgpath,base64_decode($image));
					$otp=substr(str_shuffle("0123456789"), 0,6);
					//file_get_contents("http://enterprise.smsgupshup.com/GatewayAPI/rest?method=SendMessage&send_to=$phone&msg=Your%20OTP%20for%20the%20Verification%20is%20$otp%20Thank%20you%20&msg_type=TEXT&userid=2000167123&auth_scheme=plain&password=m6sbHI75L&v=1.1&format=text");
					
					$response['status']="success";
					$response['otp']=$otp;
					echo json_encode($response);
				}else{
					$response['status']="failed";
					echo json_encode($response);
			   }
		    }else{
					$response['status']="error";
					echo json_encode($response);
			}
		
		}if ($tag == 'Login'){
		
		    $phone=$_POST['phone'];
			
			
			$query ="SELECT  * FROM `user_tbl` WHERE  (`phone`='".$phone."' and otpflag='1')";
			$r=$con->query($query);
			if ($r->num_rows>0){
				    $row=$r->fetch_assoc();
					// output data of each row
				
					$response['status']="success";
					$response['id']=$row['id'];
					$response['phone']=$row['phone'];
					$response['name']=$row['name'];
					$response['email']=$row['emailid'];
						
					echo json_encode($response);
				
		    }else{
					$response['status']="failed";
					echo json_encode($response);
			}
		
		}if ($tag == 'JoinGroup'){
			  $memid=$_POST['userid'];
			  $gpid=$_POST['gpid'];
			
				$sql ="INSERT INTO `joined_members`( `gp_id`, `user_id`, `approved`)  VALUES (".$gpid.",".$memid.",0)";
				$result = $con->query($sql);

				if ($result){
					// output data of each row
					$response['status']="success";
					
					echo json_encode($response);
				}else{
					$response['status']="failed";
					echo json_encode($response);
			   }

		}if ($tag == 'ApprovedUser'){
			  $rowid=$_POST['rowid'];
			  $adminid=$_POST['adminid'];
			
				$sql ="UPDATE `joined_members` SET `approved`=1 WHERE `id`=$rowid";
				$result = $con->query($sql);

				if ($result){
					// output data of each row
					$response['status']="success";
					
					echo json_encode($response);
				}else{
					$response['status']="failed";
					echo json_encode($response);
			   }

		}if ($tag == 'UnJoinGroup'){
			  $memid=$_POST['userid'];
			  $gpid=$_POST['gpid'];
			
				$sql ="DELETE FROM `joined_members` where  `gp_id`=".$gpid." and `user_id` =".$memid."";
				$result = $con->query($sql);

				if ($result){
					// output data of each row
					$response['status']="success";
					
					echo json_encode($response);
				}else{
					$response['status']="failed";
					echo json_encode($response);
			   }

		}if ($tag =='Updateotp'){
		
		  $phone1=$_POST['phone'];
		  	  
			
				$sql1 ="UPDATE `user_tbl` SET `otpflag`= '1' WHERE  phone='".$phone1."'";
				$result = $con->query($sql1);

				if ($result){
					// output data of each row
					$response['status']="success";
					
					echo json_encode($response);
				}else{
					$response['status']="failed";
					echo json_encode($response);
			   }
		
		}if ($tag == 'UpdateLocation'){
		
		  $memid=$_POST['userid'];
		  $lat=$_POST['latitude'];
		  $lon=$_POST['longitude'];
			  
			
				$sql ="INSERT INTO `tracking`( `user_id`, `latitude`, `longitude`) VALUES (".$memid.",'".$lat."','".$lon."')";
				$result = $con->query($sql);

				if ($result){
					// output data of each row
					$response['status']="success";
					
					echo json_encode($response);
				}else{
					$response['status']="failed";
					echo json_encode($response);
			   }
		
		}if ($tag == 'ListJoined'){
		
		    $memid=$_POST['userid'];
			$query ="SELECT a.`id`, a.`gp_id`, a.`user_id`,b.id,b.group_name FROM `joined_members` a,circles b  WHERE (a.gp_id=b.id) and a.approved=1 and a.`user_id`=$memid";
			$r=$con->query($query);
			 $i=0;
			if ($r->num_rows>0) {
			while($row=$r->fetch_assoc())	{		
					$response[$i]['status']="success";
					$response[$i]['id']=$row['id'];
					$response[$i]['groupname']=$row['group_name'];
					$response[$i]['Joinstatus']='Joined';
					$i++;
					}	
					echo json_encode($response);
				
		    }else{
					$response[$i]['status']="failed";
					echo json_encode($response);
			}
		
		}if ($tag == 'ListUnJoined'){
		    $memid=$_POST['userid'];
			$query ="SELECT cr.`group_name`,cr.id FROM `circles` cr WHERE NOT EXISTS (SELECT * from joined_members jm WHERE jm.`gp_id`=cr.`id` and  jm.approved=1 and jm.`user_id`=".$memid.")";
			$r=$con->query($query);
			 $i=0;
			if ($r->num_rows>0) {
			while($row=$r->fetch_assoc())	{		
				    
					$response[$i]['status']="success";
					$response[$i]['id']=$row['id'];
					$response[$i]['groupname']=$row['group_name'];
					$response[$i]['Joinstatus']='Not Joined';
					
					$i++;
					}	
					echo json_encode($response);
				
		    }else{
					$response[$i]['status']="failed";
					echo json_encode($response);
			}
		
		}if ($tag == 'ApprovalPending'){
		    $memid=$_POST['userid'];
			//$query ="SELECT cr.`group_name`,cr.id FROM `circles` cr WHERE NOT EXISTS (SELECT utbl.name ,jm.id as rid from joined_members jm Left Join user_tbl utbl On utbl.id=jm.user_id WHERE  jm.`approved`=1) and  cr.adminid=$memid";
			$query ="SELECT cr.`group_name`,cr.id,utbl.name ,jm.id as rid FROM `circles` cr left join joined_members jm on cr.id=jm.`gp_id` Left Join user_tbl utbl On utbl.id=jm.user_id WHERE jm.`gp_id`=cr.`id` and  jm.`approved`=0 and  cr.adminid=$memid";
			$r=$con->query($query);
			 $i=0;
			if ($r->num_rows>0) {
			while($row=$r->fetch_assoc())	{		
				    
					$response[$i]['status']="success";
					$response[$i]['id']=$row['rid'];
					$response[$i]['groupname']=$row['group_name'];
					$response[$i]['Joinstatus']='Approval Pending';
					$response[$i]['membername']=$row['name'];
					
					$i++;
					}	
					echo json_encode($response);
				
		    }else{
					$response[$i]['status']="failed";
					echo json_encode($response);
			}
		
		}
		
		if ($tag == 'NearestMem'){
		
		    $memid=$_POST['userid'] ;
		    $gpid=$_POST['gpid'];
			
			
			$query ="SELECT tracking.`id`, tracking.`user_id`,user_tbl.name,user_tbl.image,circles.`group_name`,joined_members.`gp_id`, tracking.`latitude`, tracking.`longitude`, tracking.`date_time` FROM (SELECT `id`,`user_id`,`latitude`,`longitude`, MAX(`date_time`) as maxDate FROM tracking GROUP BY `user_id` order by `date_time`desc) recent INNER JOIN tracking ON recent.maxDate = tracking.`date_time` LEFT JOIN joined_members ON joined_members.`user_id`=recent.`user_id` LEFT JOIN user_tbl ON user_tbl.id=tracking.`user_id` LEFT JOIN circles ON circles.id=joined_members.gp_id where joined_members.gp_id=".$gpid." and tracking.user_id!=".$memid." and tracking.approved=1";
			$r=$con->query($query);
			 $i=0;
			if ($r->num_rows>0) {
			while($row=$r->fetch_assoc())	{		
					$response[$i]['status']="success";
					$response[$i]['id']=$row['id'];
					$response[$i]['groupname']=$row['group_name'];
					$response[$i]['name']=$row['name'];
					$phpdate = strtotime( $row['date_time'] );
                $mysqldate = date( 'Y-m-d H:i:s', $phpdate );

					$response[$i]['lastseen']=$mysqldate;
					$response[$i]['latitude']=$row['latitude'];
					$response[$i]['image']=$row['image'];
					$response[$i]['longitude']=$row['longitude'];
					$i++;
					}	
					echo json_encode($response);
		    }else{
					$response[$i]['status']="failed";
					echo json_encode($response);
			}
		}if ($tag == 'GetGroups'){
		    $memid=$_POST['userid'];
			$query ="SELECT a.id,a.`group_name` ,b.`gp_id`, b.`user_id` FROM `circles` a Left Join `joined_members` b ON a.id=b.gp_id LEFT JOIN user_tbl c ON c.id=b.user_id where b.user_id=".$memid." and b.approved=1 group by b.gp_id";
			$r=$con->query($query);
			 $i=0;
			if ($r->num_rows>0) {
			while($row=$r->fetch_assoc())	{		
				    
					$response[$i]['status']="success";
					$response[$i]['id']=$row['id'];
					$response[$i]['groupname']=$row['group_name'];
					$i++;
					}	
					echo json_encode($response);
				
		    }else{
					$response[$i]['status']="failed";
					echo json_encode($response);
			}
		
		}if ($tag == 'Newgroup'){
		    $memid=$_POST['memid'];
			$gpname=$_POST['gpname'];
			$query ="SELECT  * FROM `circles` WHERE `group_name`='".$gpname."' ";
			$r=$con->query($query);
			if ($r->num_rows ==0) {
				$sql ="INSERT INTO `circles`( `group_name`,`adminid`) VALUES ('".$gpname."',$memid)";
				$result = $con->query($sql);

				if ($result){
					// output data of each row
					$query1 ="SELECT  * FROM `circles` WHERE  `group_name`='".$gpname."'";
			        $r1=$con->query($query1);
			        if ($r1->num_rows>0) {
				    $row=$r1->fetch_assoc();
					$res=$row['id'];
                  
						$sql1 ="INSERT INTO `joined_members`( `gp_id`, `user_id`, `approved`)  VALUES ($res,$memid,1)";
						$result1 = $con->query($sql1);
							if ($result1){
							
							//echo 'tt';
							$response['status']="success";
							echo json_encode($response);
							
							}else{
							$response['status']="No insert to joined_members table ";
							echo json_encode($response);
							
							}
					}else{
					    $response['status']="no gp name present";
				    	echo json_encode($response);
			    }
		    }else{
					$response['status']="no insert to gp table";
					echo json_encode($response);
			}}else{
					$response['status']="gp name already present";
					echo json_encode($response);
			}
    	}
	}
?>