<?php
	if(isset($_POST['submit'])){
		$name=$_POST['name'];
		$email=$_POST['email'];
		$msg=$_POST['msg'];
		
		$to='adrianguevaralonso@gmail.com';
		$subject='MENSAJE DE MI PAGINA WEB';
		$message="Nombre: ".$name."\n"."Ha escrito lo siguiente: "."\n\n".$msg;
		$headers="From: ".$email;
		
		/*if(mail($to, $subject, $message, $headers)){
			echo "<h1>Su mensaje se ha enviado correctamente"." ".$name.", ¡Contactar&eacute; con usted pronto!</h1>";
		}
		else{
			echo "¡Algo ha ido mal!";
		}*/
	}
?>