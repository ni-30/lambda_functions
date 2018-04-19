function FusionPlayGameOneConsole(canvas) {
    var ctx = canvas.getContext("2d");
    var ballRadius , x , y , dx ,dy ,paddleHeight ,paddleWidth ,paddleX ,rightPressed ,leftPressed ,brickRowCount ,brickColumnCount ,brickWidth ,brickHeight ,brickPadding ,brickOffsetTop ,brickOffsetLeft ,score ,lives ,bricks;
    
    function mouseMoveHandler(e) {
        var relativeX = e.clientX - canvas.offsetLeft;
        if (relativeX > 0 && relativeX < canvas.width) {
            paddleX = relativeX - paddleWidth / 2;
        }
    }

    function collisionDetection() {
        for (c = 0; c < brickColumnCount; c++) {
            for (r = 0; r < brickRowCount; r++) {
                var b = bricks[c][r];
                if (b.status == 1) {
                    if (x > b.x && x < b.x + brickWidth && y > b.y && y < b.y + brickHeight) {
                        dy = -dy;
                        b.status = 0;
                        score++;
                        if (score == brickRowCount * brickColumnCount) {
                            this.stop();
                            document.getElementById("modal-text-value").innerHTML = "Congrats! You won";
                            $('#myModal').modal('show');
                        }
                    }
                }
            }
        }
    }

    function drawBall() {
        ctx.beginPath();
        ctx.arc(x, y, ballRadius, 0, Math.PI * 2);
        ctx.fillStyle = "#0095DD";
        ctx.fill();
        ctx.closePath();
    }

    function drawPaddle() {
        ctx.beginPath();
        ctx.rect(paddleX, canvas.height - paddleHeight, paddleWidth, paddleHeight);
        ctx.fillStyle = "#0095DD";
        ctx.fill();
        ctx.closePath();
    }

    function drawBricks() {
        for (c = 0; c < brickColumnCount; c++) {
            for (r = 0; r < brickRowCount; r++) {
                if (bricks[c][r].status == 1) {
                    var brickX = (r * (brickWidth + brickPadding)) + brickOffsetLeft;
                    var brickY = (c * (brickHeight + brickPadding)) + brickOffsetTop;
                    bricks[c][r].x = brickX;
                    bricks[c][r].y = brickY;
                    ctx.beginPath();
                    ctx.rect(brickX, brickY, brickWidth, brickHeight);
                    ctx.fillStyle = "#0095DD";
                    ctx.fill();
                    ctx.closePath();
                }
            }
        }
    }

    function drawScore() {
        ctx.font = "17px Arial";
        ctx.fillStyle = "#0095DD";
        ctx.fillText("Score: " + score, 70, 20);
    }

    function drawLives() {
        ctx.font = "17px Arial";
        ctx.fillStyle = "#0095DD";
        ctx.fillText("Remaining Lives: " + lives, canvas.width - 200, 20);
    }

    // load config
    function init() {
        ballRadius = 20;
        x = canvas.width / 2;
        y = canvas.height - 30;
        dx = 1;
        dy = -1;
        paddleHeight = 10;
        paddleWidth = 150;
        paddleX = (canvas.width - paddleWidth) / 2;
        rightPressed = false;
        leftPressed = false;
        brickRowCount = 15;
        brickColumnCount = 6;
        brickWidth = 66;
        brickHeight = 7;
        brickPadding = 5;
        brickOffsetTop = 30;
        brickOffsetLeft = 28;
        score = 0;
        lives = 10;
        bricks = [];

        for (c = 0; c < brickColumnCount; c++) {
            bricks[c] = [];
            for (r = 0; r < brickRowCount; r++) {
                bricks[c][r] = {
                    x: 0,
                    y: 0,
                    status: 1
                };
            }
        }
        
    }
    
    // create new screen for canvas animation
    function screen() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        drawBricks();
        drawBall();
        drawPaddle();
        drawScore();
        drawLives();
        collisionDetection();
    }
    

    var isCreated = false;
    var isPlaying = false;
    var isPaused = true;
    var isDestroyed = false;

    var stepDiffL = 0;
    var stepDiffR = 0;
    // start canvas animation
    function animate() {
        if(isPaused || isDestroyed) {
            return;
        }

        try {
            screen();

            if (x + dx > canvas.width - ballRadius || x + dx < ballRadius) {
                dx = -dx;
            }
            if (y + dy < ballRadius) {
                dy = -dy;
            } else if (y + dy > canvas.height - ballRadius) {
                if (x > paddleX && x < paddleX + paddleWidth) {
                dy = -dy;
                } else {
                lives--;
                if (!lives) {
                    document.getElementById("modal-text-value").innerHTML = "GAME OVER";
                    this.stop();
                } else {
                    x = canvas.width / 2;
                    y = canvas.height - 30;
                    dx = 1;
                    dy = -1;
                    paddleX = (canvas.width - paddleWidth) / 2;
                }
                }
            }

            if (rightPressed && paddleX < canvas.width - paddleWidth) {
                paddleX += 7;
                if(++stepDiffR > 4) {
                    rightPressed = false;
                    stepDiffR = 0;
                }
            } else if (leftPressed && paddleX > 0) {
                paddleX -= 7;
                if(++stepDiffL > 4) {
                    leftPressed = false;
                    stepDiffL = 0;
                }
            }

            x += dx;
            y += dy;
        } finally {
            requestAnimationFrame(animate);
        }
    }

    this.controller = function(key) {
        rightPressed =  !isPaused &&  key == "RIGHT";
        leftPressed =  !isPaused &&  key == "LEFT";
    }

    this.create = function() {
        init();
        screen();
        isCreated = true;
    }
    
    this.play = function() {
        if(isDestroyed || isPlaying) {
            if(isPaused) {
                this.resume();
            }
            return;
        }
        init();
        isPlaying = true;
        isPaused = false;
        animate();
    }

    this.pause = function() {
        isPaused = true;
    }
    
    this.resume = function() {
        if(isPlaying) {
            isPaused = false;
        }
        animate();
    }
    
    this.stop = function() {
        isPaused = true;
        isPlaying = false;
    }

    this.destroy = function() {
        isPaused = true;
        isPlaying = false;
        isDestroyed = true;
    }

    this.isDestroyed = function() {
        return isDestroyed;
    }

    this.isPaused = function() {
        return isPaused;
    }

    this.isPlaying = function() {
        return isPlaying;
    }

}