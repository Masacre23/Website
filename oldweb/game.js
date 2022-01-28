var pause = false;
(function (window, undefined) {
	'use strict';
	var canvas = null,
		ctx = null,
		/*buffer = null,
    	bufferCtx = null,
		bufferScale = 1,
    	bufferOffsetX = 0,
    	bufferOffsetY = 0,*/
		scaleX = 1, scaleY = 1,

		lastPress = null,
		pressing = [],
		KEY_ENTER= 13,
		KEY_LEFT = 37,
		KEY_UP = 38,
		KEY_RIGHT = 39,
		KEY_DOWN = 40,

		lastUpdate = Date.now(),

		//pause = false,
		player = null,
		elapsedTime=0,
		playerSpriteArea = [],
		skin = new Image(),
		background = new Image(),
		contract = null,
		contractImg = new Image(),
		score = 0,
		wall = [],
		door = [],
		doorColliding = [],
		windowToOpen = ['EducationSkills', 'EducationSkills', 'Projects', 'Projects', 'About', 'About', 'Contact'],
		gameover = false,
		fullscreen = false,

		i = 0, l = 0;

	window.requestAnimationFrame = (function () {
    	return window.requestAnimationFrame ||
        	window.mozRequestAnimationFrame ||
        	window.webkitRequestAnimationFrame ||
        	function (callback) {
				window.setTimeout(callback, 17);
			};
	}());

	document.addEventListener('keydown', function(evt)
	{
		//if (evt.which >= 37 && evt.which <= 40) {
       //     evt.preventDefault();
       // }
		//lastPress = evt.which;
		lastPress=evt.keyCode;
        pressing[evt.keyCode]=true;
	}, false);

	document.addEventListener('keyup',function(evt){
        pressing[evt.keyCode]=false;
    },false);

	function init()
	{
    	canvas = document.getElementById('canvas');
    	ctx = canvas.getContext('2d');

		// Load buffer
//		buffer = document.createElement('canvas');
    //	bufferCtx = buffer.getContext('2d');
    	//buffer.width = 1920;
		//buffer.height = 949;
	//	buffer.width = '100%';
	//	buffer.height = '100%';

		//Load assets
		skin.src = 'avatar.png';
		background.src = 'map.png';
		playerSpriteArea.push(0); //x
		playerSpriteArea.push(0); //y

		contractImg.src = 'contrato.png';

		//Initialize variables
		player = new Rectangle(50, 50, 30, 30);
		contract = new Rectangle(200,200, 20, 20);

		/*wall.push(new Rectangle(0, 0, 30, 30));
		wall.push(new Rectangle(200, 200, 30, 30));
    	wall.push(new Rectangle(400, 100, 30, 30));
		wall.push(new Rectangle(400, 200, 30, 30));*/

		//door.push(new Rectangle(300, 200, 30, 30));
		doorColliding.push(false);
		doorColliding.push(false);
		doorColliding.push(false);
		doorColliding.push(false);
		doorColliding.push(false);
		doorColliding.push(false);
		doorColliding.push(false);
		resize();
		player.x = canvas.width/10;
		player.y = canvas.height/15;
		contract.x = canvas.width/5;
		contract.y = canvas.height/2;
		//Start game
		run();
	}

	function run()
	{
		window.requestAnimationFrame(run);
		var now = Date.now();
		var dt = now - lastUpdate;
		lastUpdate = now;

		// Pause/Unpause
		if (lastPress === KEY_ENTER) {
			pause = !pause;
			lastPress = null;
    	}

		if(!pause)
		{
			update(dt);
		}

		if(gameover)
		{
			reset();
		}

		draw(ctx, dt);
		/*draw(bufferCtx, dt);
		ctx.fillStyle = '#000';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        ctx.imageSmoothingEnabled = false;
        ctx.drawImage(buffer, bufferOffsetX, bufferOffsetY, buffer.width * bufferScale, buffer.height * bufferScale);*/
		lastPress = null;
	}

	function draw(ctx, dt)
	{
		//Clean
		ctx.fillStyle = '#030';
		//ctx.fillRect(0, 0, buffer.width, buffer.height);
		ctx.fillRect(0, 0, canvas.width, canvas.height);

		// Elapsed time
        elapsedTime += dt/200;
        if(elapsedTime > 3600)
		{
            elapsedTime -= 3600;
		}

		//Draw map
		//ctx.drawImage(background, 0, 0, buffer.width, buffer.height);
		ctx.drawImage(background, 0, 0, canvas.width / scaleX, canvas.height / scaleY);

		//Draw Name
		ctx.font= canvas.width/70 + "px pixel";
		//ctx.fillStyle = 'rgba(75,128,75,0.5)';
		ctx.fillStyle = 'rgba(255,255,255,0.6)';
		ctx.fillText('Adrian', canvas.width/50, canvas.height/10);
		ctx.fillText('Guevara Alonso', canvas.width/50, canvas.height/7);

		ctx.font= canvas.width/100 + "px pixel";
		ctx.fillStyle = 'rgba(125, 93, 59, 0.6)';
		ctx.fillText('Game & Web Developer', canvas.width/50, canvas.height/5.6);

		//Draw player
		ctx.fillStyle = '#0f0';

    	//player.fill(ctx);
		if(pressing[KEY_LEFT])
		{
            playerSpriteArea[0] = (~~(elapsedTime * 1) % 4) * 32;
			playerSpriteArea[1] = 32;
		}
		else if(pressing[KEY_RIGHT])
		{
            playerSpriteArea[0] = (~~(elapsedTime * 1) % 4) * 32;
			playerSpriteArea[1] = 64;
		}
		else if(pressing[KEY_UP])
		{
			playerSpriteArea[0] = (~~(elapsedTime * 1) % 4) * 32;
			playerSpriteArea[1] = 96;
		}
		else if(pressing[KEY_DOWN])
		{
            playerSpriteArea[0] = (~~(elapsedTime * 1) % 4) * 32;
			playerSpriteArea[1] = 0;
		}

		for (i = 0, l = door.length; i < l; i += 1)
		{
			if(doorColliding[i])
				break;
		}
		if(!doorColliding[i] || !pause)
			player.drawImageArea(ctx, skin, playerSpriteArea[0], playerSpriteArea[1], 32, 32);

		//Draw contract
		//ctx.fillStyle = '#f00';
		ctx.drawImage(contractImg, contract.x, contract.y, contract.width, contract.height);

		// Draw walls
    	/*ctx.fillStyle = 'rgba(128, 128, 128, 0.5)';
    	for (var i = 0, l = wall.length; i < l; i += 1) {
			wall[i].fill(ctx);
		}

		//Draw doors
		ctx.fillStyle = '#f00';
		for (var i = 0, l = door.length; i < l; i += 1) {
			door[i].fill(ctx);
		}
		*/
		//Draw score
		/*ctx.fillStyle = '#fff';
		ctx.fillText('Score: ' + score, 0, 10);
		ctx.fillText('Canvas: ' + canvas.width, 0, 30);
		ctx.fillText('Pos: ' + player.x, 0, 40);*/

		//Draw pause
		/*if(pause)
		{
			ctx.textAlign = 'center';
			if (gameover)
			{
				ctx.fillText('GAME OVER', 150, 75);
			} else
			{
				ctx.fillText('PAUSE', 150, 75);
			}
			ctx.textAlign = 'left';
		}*/
	}

	function update(dt)
	{
		//resize();
		if(gameover)
		{
			reset();
		}


		// Change Direction
		if(pressing[KEY_UP])
		{
            player.y -= (canvas.height/928) * dt/10;
			for (i = 0, l = wall.length; i < l; i += 1) {
                if (player.intersects(wall[i])) {
					player.y += (canvas.height/928) * dt/10;
                }
            }
		}
        if(pressing[KEY_RIGHT])
		{
            player.x += (canvas.height/928) * dt/10;
			for (i = 0, l = wall.length; i < l; i += 1) {
                if (player.intersects(wall[i])) {
                    player.x -= (canvas.height/928) * dt/10;
                }
            }
		}
        if(pressing[KEY_DOWN])
		{
            player.y += (canvas.height/928) * dt/10;
			for (i = 0, l = wall.length; i < l; i += 1) {
                if (player.intersects(wall[i])) {
                    player.y -= (canvas.height/928) * dt/10;
                }
            }
		}
        if(pressing[KEY_LEFT])
		{
            player.x -= (canvas.height/928) * dt/10;
			for (i = 0, l = wall.length; i < l; i += 1) {
                if (player.intersects(wall[i])) {
                    player.x += (canvas.height/928) * dt/10;
                }
            }
		}

		// Out Screen
    	if (player.x > canvas.width) {
			player.x = 0;
    	}
    	if (player.y > canvas.height) {
			player.y = 0;
    	}
    	if (player.x < 0) {
			player.x = canvas.width;
    	}
		if (player.y < 0) {
			player.y = canvas.height;
    	}

		// Money Intersects
    	if (player.intersects(contract)) {
			score += 1;
			contract.x = random(canvas.width / 10 - 1) * 10;
			contract.y = random(canvas.height / 10 - 1) * 10;
    	}

		// Wall Intersects
		for (i = 0, l = wall.length; i < l; i += 1)
		{
        	if (contract.intersects(wall[i])) {
				contract.x = random(canvas.width / 10 - 1) * 10;
				contract.y = random(canvas.height / 10 - 1) * 10;
        	}

        	//if (player.intersects(wall[i])) {

				//gameover = true;
				//pause = true;
        	//}
		}

		//Door intersect
		for (i = 0, l = door.length; i < l; i += 1)
		{
        	if (player.intersects(door[i])) {
				if(!doorColliding[i])
				{
					toggle(windowToOpen[i], true);
					//pause = true;
					doorColliding[i] = true;
				}
        	}
			else
				doorColliding[i] = false;
		}
	}

	function Rectangle(x, y, width, height) {
		this.x = (x === undefined) ? 0 : x;
        this.y = (y === undefined) ? 0 : y;
        this.width = (width === undefined) ? 0 : width;
        this.height = (height === undefined) ? this.width : height;
	}

	Rectangle.prototype = {
		constructor: Rectangle,

		intersects: function (rect) {
            if (rect === undefined) {
                window.console.warn('Missing parameters on function intersects');
            } else {
                return (this.x < rect.x + rect.width &&
                    this.x + this.width > rect.x &&
                    this.y < rect.y + rect.height &&
                    this.y + this.height > rect.y);
            }
        },

        fill: function (ctx) {
            if (ctx === undefined) {
                window.console.warn('Missing parameters on function fill');
            } else {
                ctx.fillRect(this.x, this.y, this.width, this.height);
            }
        },

        drawImage: function (ctx, img) {
            if (img === undefined) {
                window.console.warn('Missing parameters on function drawImage');
            } else {
                if (img.width) {
                    ctx.drawImage(img, this.x, this.y, this.width, this.height);
                } else {
                    ctx.strokeRect(this.x, this.y, this.width, this.height);
                }
            }
        },

		drawImageArea: function(ctx,img,sx,sy,sw,sh){
        	if(img.width){
					ctx.drawImage(img,sx,sy,sw,sh,this.x,this.y,this.width,this.height);
			}else{
            	ctx.strokeRect(this.x,this.y,this.width,this.height);
			}
    	}
	};

	function random(max) {
    	return Math.floor(Math.random() * max);
	}

	function reset()
	{
		score = 0;
		/*player.x = random(buffer.width / 10 - 1) * 10;
    	player.y = random(buffer.height / 10 - 1) * 10;
		money.x = random(buffer.width / 10 - 1) * 10;
    	money.y = random(buffer.height / 10 - 1) * 10;*/
    	gameover = false;
	}

	function resize()
	{
		canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;



		player.width = canvas.width/62;
		player.height = canvas.height/29;
		contract.width = 0.75 * canvas.width/60;
		contract.height = 0.75 * canvas.height/29;

		var sizeY = canvas.height/29;
		var sizeX = canvas.width/60;
		//Doors
		door.length = 0;
		door.push(new Rectangle(3 * sizeX, 18 * sizeY, 1 * sizeX, 0.35 * sizeY));
		door.push(new Rectangle(10 * sizeX, 22 * sizeY, 1 * sizeX, 0.35 * sizeY));
		door.push(new Rectangle(22 * sizeX, 12 * sizeY, 1 * sizeX, 0.35 * sizeY));
		door.push(new Rectangle(25 * sizeX, 10 * sizeY, 1 * sizeX, 0.35 * sizeY));
		door.push(new Rectangle(33 * sizeX, 10 * sizeY, 1 * sizeX, 0.35 * sizeY));
		door.push(new Rectangle(36 * sizeX, 12 * sizeY, 1 * sizeX, 0.35 * sizeY));
		door.push(new Rectangle(52 * sizeX, 8 * sizeY, 1 * sizeX, 0.35 * sizeY));

		wall.length = 0;

		var offsetX = 0.6 * sizeX;
		var offsetY = - 0.75 * sizeY;

		//Primer recuadro
		wall.push(new Rectangle(1 * sizeX, 9 * sizeY, 1 * sizeX, sizeY + offsetY));
		wall.push(new Rectangle(12 * sizeX, 9 * sizeY, 1 * sizeX, sizeY + offsetY));
		wall.push(new Rectangle(13 * sizeX, 11 * sizeY, 1 * sizeX, sizeY + offsetY));

		//Segundo recuadro
		wall.push(new Rectangle(15 * sizeX + offsetX/2, 0 * sizeY, 12 * sizeX - offsetX, sizeY + offsetY));
		wall.push(new Rectangle(15 * sizeX + offsetX/2, 0 * sizeY, 1 * sizeX - offsetX, 5 * sizeY + offsetY));
		wall.push(new Rectangle(26 * sizeX + offsetX/2, 0 * sizeY, 1 * sizeX - offsetX, 5 * sizeY + offsetY));
		wall.push(new Rectangle(15 * sizeX + offsetX/2, 6 * sizeY, 1 * sizeX - offsetX, 7 * sizeY + offsetY));
		wall.push(new Rectangle(15 * sizeX + offsetX/2, 12 * sizeY, 7 * sizeX - offsetX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(23 * sizeX, 12 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(21 * sizeX, 4 * sizeY, 6 * sizeX, 6 * sizeY + offsetY));
		wall.push(new Rectangle(21 * sizeX, 10 * sizeY, 3 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(24 * sizeX, 10 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(26 * sizeX, 10 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(19 * sizeX, 10 * sizeY, 2 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(16 * sizeX, 8 * sizeY, 2 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(17 * sizeX, 1 * sizeY, 2 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(23 * sizeX, 1 * sizeY, 2 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(20 * sizeX, 8 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));

		//Tercer recuadro
		wall.push(new Rectangle(32 * sizeX + offsetX/2, 0 * sizeY, 6 * sizeX - offsetX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(39 * sizeX + offsetX/2, 0 * sizeY, 6 * sizeX - offsetX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(32 * sizeX + offsetX/2, 1 * sizeY, 1 * sizeX - offsetX, 5 * sizeY + offsetY));
		wall.push(new Rectangle(44 * sizeX + offsetX/2, 1 * sizeY, 1 * sizeX - offsetX, 4 * sizeY + offsetY));
		wall.push(new Rectangle(44 * sizeX + offsetX/2, 6 * sizeY, 1 * sizeX - offsetX, 7 * sizeY + offsetY));
		wall.push(new Rectangle(38 * sizeX, 12 * sizeY, 6 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(32 * sizeX, 5 * sizeY, 6 * sizeX, 5 * sizeY + offsetY));
		wall.push(new Rectangle(32 * sizeX, 10 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(34 * sizeX, 10 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(35 * sizeX, 10 * sizeY, 3 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(35 * sizeX, 10 * sizeY, 3 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(35 * sizeX, 12 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(37 * sizeX, 12 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));

		wall.push(new Rectangle(39 * sizeX, 11 * sizeY, 3 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(40 * sizeX, 9 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(39 * sizeX, 6 * sizeY, 1 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(42 * sizeX, 6 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));

		//Cuarto recuadro
		wall.push(new Rectangle(46 * sizeX, 0 * sizeY, 5 * sizeX, 13 * sizeY + offsetY));
		wall.push(new Rectangle(54 * sizeX, 0 * sizeY, 6 * sizeX, 13 * sizeY + offsetY));
		wall.push(new Rectangle(51 * sizeX, 0 * sizeY, 3 * sizeX, 8 * sizeY + offsetY));
		wall.push(new Rectangle(51 * sizeX, 8 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(53 * sizeX, 8 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));

		//Quinto recuadro
		wall.push(new Rectangle(2 * sizeX, 16 * sizeY, 3 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(2 * sizeX, 16 * sizeY, 3 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(2 * sizeX, 18 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(4 * sizeX, 18 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));

		wall.push(new Rectangle(9 * sizeX, 20 * sizeY, 3 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(9 * sizeX, 20 * sizeY, 3 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(9 * sizeX, 22 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(11 * sizeX, 22 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));

		wall.push(new Rectangle(5 * sizeX, 20 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(3 * sizeX, 23 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(4 * sizeX, 28 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		//wall.push(new Rectangle(10 * sizeX, 25 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));

		//Sexto recuadro
		wall.push(new Rectangle(15 * sizeX + offsetX/2, 16 * sizeY, 8 * sizeX - offsetX, 5 * sizeY + offsetY));
		wall.push(new Rectangle(15 * sizeX + offsetX/2, 21 * sizeY, 1 * sizeX - offsetX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(15 * sizeX + offsetX/2, 24 * sizeY, 1 * sizeX - offsetX, 5 * sizeY));
		wall.push(new Rectangle(16 * sizeX, 28 * sizeY, 10 * sizeX, 1 * sizeY ));
		wall.push(new Rectangle(26 * sizeX + offsetX/2, 24 * sizeY, 1 * sizeX - offsetX, 5 * sizeY));
		wall.push(new Rectangle(26 * sizeX + offsetX/2, 18 * sizeY, 1 * sizeX - offsetX, 5 * sizeY + offsetY));
		wall.push(new Rectangle(23 * sizeX, 18 * sizeY, 3 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(23 * sizeX, 16 * sizeY, 1 * sizeX, 2 * sizeY + offsetY));

		wall.push(new Rectangle(24 * sizeX, 19 * sizeY, 2 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(23 * sizeX, 21 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(25 * sizeX, 24 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(20 * sizeX, 24 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(23 * sizeX, 27 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));

		//Septimo recuadro
		wall.push(new Rectangle(32 * sizeX + offsetX/2, 24 * sizeY, 1 * sizeX - offsetX, 6 * sizeY + offsetY));
		wall.push(new Rectangle(33 * sizeX + offsetX/2, 28 * sizeY, 5 * sizeX - offsetX, 1 * sizeY));
		wall.push(new Rectangle(39 * sizeX + offsetX/3, 24 * sizeY, 6 * sizeX - offsetX, 6 * sizeY + offsetY));
		wall.push(new Rectangle(39 * sizeX + offsetX/2, 16 * sizeY, 6 * sizeX - offsetX, 4 * sizeY + offsetY));
		wall.push(new Rectangle(44 * sizeX + offsetX/2, 19 * sizeY, 1 * sizeX - offsetX, 4 * sizeY + offsetY));
		wall.push(new Rectangle(32 * sizeX + offsetX/2, 18 * sizeY, 4 * sizeX - offsetX, 4 * sizeY + offsetY));
		wall.push(new Rectangle(32 * sizeX + offsetX/2, 22 * sizeY, 1 * sizeX - offsetX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(35 * sizeX + offsetX/2, 16 * sizeY, 1 * sizeX - offsetX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(36 * sizeX + offsetX/2, 16 * sizeY, 2 * sizeX - offsetX, 1 * sizeY + offsetY));

		wall.push(new Rectangle(37 * sizeX, 17 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));

		//Octavo reciadro
		wall.push(new Rectangle(46 * sizeX, 16 * sizeY, 6 * sizeX, 3 * sizeY + offsetY));
		wall.push(new Rectangle(46 * sizeX, 20 * sizeY, 5 * sizeX, 3 * sizeY + offsetY));
		wall.push(new Rectangle(55 * sizeX, 16 * sizeY, 6 * sizeX, 4 * sizeY + offsetY));
		wall.push(new Rectangle(53 * sizeX, 22 * sizeY, 7 * sizeX, 4 * sizeY + offsetY));

		wall.push(new Rectangle(54 * sizeX, 18 * sizeY, 1 * sizeX, 2 * sizeY + offsetY));
		wall.push(new Rectangle(52 * sizeX, 20 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));
		wall.push(new Rectangle(49 * sizeX, 25 * sizeY, 1 * sizeX, 1 * sizeY + offsetY));

		//Rio
		wall.push(new Rectangle(28 * sizeX, 0 * sizeY, 3 * sizeX, 5 * sizeY + offsetY));
		wall.push(new Rectangle(28 * sizeX, 6 * sizeY, 3 * sizeX, 7 * sizeY + offsetY));
		wall.push(new Rectangle(28 * sizeX, 16 * sizeY, 3 * sizeX, 7 * sizeY + offsetY));
		wall.push(new Rectangle(28 * sizeX, 24 * sizeY, 3 * sizeX, 7 * sizeY + offsetY));
		/*for (i = 0, l = wall.length; i < l; i += 1) {
            wall[i].width = canvas.width/60;
			wall[i].height = canvas.height/30;
        }*/
	}

	window.addEventListener('resize', resize, false);
	window.addEventListener('load', init, false);// JavaScript Document
}(window));