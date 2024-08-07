package com.ezlifesol.game.snake

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezlifesol.game.snake.obj.Fruit
import com.ezlifesol.game.snake.obj.Grass
import com.ezlifesol.game.snake.obj.Snake
import com.ezlifesol.library.gampose.collision.collider.CircleCollider
import com.ezlifesol.library.gampose.compose.GameObject
import com.ezlifesol.library.gampose.compose.GameSpace
import com.ezlifesol.library.gampose.compose.GameSprite
import com.ezlifesol.library.gampose.input.detectDragging
import com.ezlifesol.library.gampose.media.audio.AudioManager
import com.ezlifesol.library.gampose.unit.GameSize
import com.ezlifesol.library.gampose.unit.GameVector
import kotlin.math.abs
import kotlin.math.roundToInt

// Constants representing the number of cells in the grid along the X and Y axes
const val cellNumberX = 12
var cellNumberY = cellNumberX

/**
 * Composable function representing the Snake game screen.
 * It sets up the game space, initializes game objects, and handles game logic and input.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnakeScreen() {
    val moveRate = 0.2f // Rate at which the snake moves
    var nextMove by remember { mutableFloatStateOf(0f) } // Time for the next move

    var snakeDirection by remember { mutableStateOf(GameVector(1f, 0f)) } // Initial direction of the snake

    val fruit by remember { mutableStateOf(Fruit.create()) } // Initialize fruit
    val snake by remember { mutableStateOf(Snake.create()) } // Initialize snake

    GameSpace(
        modifier = Modifier.fillMaxSize() // Fill the available screen space
    ) {
        val score = snake.listBody.size - 3
        // Calculate the size of each grid cell
        val cellSize = gameSize.width / cellNumberX
        cellNumberY = (cellNumberX * (gameSize.height / gameSize.width)).roundToInt() // Adjust the number of cells in the Y direction
        gameOutfit.background = Color(175, 215, 70) // Set background color

        // Set the size of the grass, fruit, and snake based on the cell size
        val grass by remember { mutableStateOf(Grass.create()) } // Initialize grass
        grass.size = GameSize(cellSize, cellSize)
        if (fruit.size == GameSize.zero) {
            fruit.size = GameSize(cellSize, cellSize)
            fruit.randomize()
        }
        snake.size = GameSize(cellSize, cellSize)

        // Render each grass tile
        grass.listGrass.forEach { position ->
            GameObject(
                size = grass.size,
                position = grass.getGrass(position),
                color = Color(167, 209, 61)
            )
        }

        // Render each segment of the snake
        snake.listBody.forEachIndexed { index, body ->
            val sprite = if (index == snake.listBody.size - 1) {
                snake.headSprite() // Head of the snake
            } else if (index == 0) {
                snake.tailSprite() // Tail of the snake
            } else {
                snake.bodySprite(index, body) // Body of the snake
            }
            GameSprite(
                assetPath = sprite,
                size = snake.size,
                position = snake.getBody(body),
            )
        }

        // Render the fruit
        GameSprite(
            assetPath = fruit.sprite,
            size = fruit.size,
            position = fruit.position,
        )

        // Move the snake if the time has reached the next move time
        if (gameTime > nextMove && snake.isGameOver.not()) {
            snake.direction = snakeDirection
            snake.moveSnake()
            nextMove = gameTime + moveRate
        }

        // Check for collision with the fruit
        if (fruit.checkCollision(snake.listBody)) {
            AudioManager.playSound(R.raw.crunch) // Play sound on collision
            snake.addBlock() // Add a new block to the snake
        }

        // Handle dragging input to change the snake's direction
        gameInput.onDragging = detectDragging(onDrag = { _, direction ->
            val absX = abs(direction.x)
            val absY = abs(direction.y)

            val prevDirection = snakeDirection
            snakeDirection = when {
                direction.x > 0 && absX > absY -> GameVector(1f, 0f)  // Right
                direction.x < 0 && absX > absY -> GameVector(-1f, 0f) // Left
                direction.y > 0 && absY > absX -> GameVector(0f, 1f)  // Up
                direction.y < 0 && absY > absX -> GameVector(0f, -1f) // Down
                else -> snakeDirection
            }
            if (snake.direction == GameVector.zero - snakeDirection) {
                snakeDirection = prevDirection
            }
        })

        Text(
            text = "Score: $score",
            modifier = Modifier.padding(16.dp),
            color = Color.Black,
            fontSize = 20.sp
        )

        if(snake.isGameOver) {
            BasicAlertDialog(onDismissRequest = { }) {
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.3f) // nền tối
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = """
                        Game Over
                        Your Score: $score
                    """.trimIndent(),
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton(
                            onClick = {
                                snake.reset()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.White,
                                containerColor =  Color(175, 215, 70)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Replay")
                        }
                    }
                }
            }
        }
    }
}
