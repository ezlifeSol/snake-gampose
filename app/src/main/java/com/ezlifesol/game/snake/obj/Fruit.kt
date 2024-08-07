package com.ezlifesol.game.snake.obj

import androidx.annotation.Keep
import com.ezlifesol.game.snake.cellNumberX
import com.ezlifesol.game.snake.cellNumberY
import com.ezlifesol.library.gampose.unit.GameSize
import com.ezlifesol.library.gampose.unit.GameVector
import kotlin.random.Random

/**
 * Class representing a Fruit in the game.
 * It contains properties and methods to manage the fruit's position, size, and collision detection with the snake.
 */
@Keep
class Fruit {
    /**
     * The sprite representing the fruit.
     * Initialized to "apple.webp".
     */
    val sprite = "apple.webp"

    /**
     * The x-coordinate of the fruit's position in the grid.
     * Initialized to 0.
     */
    private var x: Int = 0

    /**
     * The y-coordinate of the fruit's position in the grid.
     * Initialized to 0.
     */
    private var y: Int = 0

    /**
     * The position of the fruit as a GameVector.
     * Initialized to GameVector.zero.
     */
    var position: GameVector = GameVector.zero

    /**
     * The size of the fruit.
     * Initialized to GameSize.zero.
     */
    var size: GameSize = GameSize.zero

    /**
     * Companion object for the Fruit class.
     * Contains a factory method to create and initialize a Fruit object.
     */
    companion object {
        /**
         * Factory method to create and initialize a Fruit object.
         * @return a Fruit object with a randomized position.
         */
        fun create(): Fruit {
            val fruit = Fruit()
            fruit.randomize()  // Randomize the position of the fruit
            return fruit
        }
    }

    /**
     * Randomize the position of the fruit within the grid.
     * The position is scaled based on the size of the grid cells.
     */
    fun randomize() {
        x = Random.nextInt(0, cellNumberX - 1)
        y = Random.nextInt(0, cellNumberY - 1)
        position = GameVector(size.width * x, size.height * y)
    }

    /**
     * Check for a collision between the fruit and the snake.
     * If the snake's head is at the same position as the fruit, randomize the fruit's position to avoid collision with the snake's body.
     * @param snake the list of GameVector positions representing the snake's body segments.
     * @return true if there is a collision; false otherwise.
     */
    fun checkCollision(snake: MutableList<GameVector>): Boolean {
        val snakeHead = snake.last()
        var isRandom = true
        if (snakeHead.x == x.toFloat() && snakeHead.y == y.toFloat()) {
            while (isRandom) {
                randomize()
                snake.find {
                    it.x == x.toFloat() && it.y == y.toFloat()
                } ?: run {
                    isRandom = false
                }
            }
            return true
        }
        return false
    }
}
