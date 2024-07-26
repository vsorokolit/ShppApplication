package com.kotik.shppapplication

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kotik.shppapplication.databinding.ActivitySignUpBinding
import com.kotik.shppapplication.utils.MyImg
import com.kotik.shppapplication.utils.PasswordTransformation
import com.kotik.shppapplication.utils.changeTextToDots
import com.kotik.shppapplication.utils.log


private const val MIN_EMAIL_LEN = 8
private const val MIN_PASSWORD_LEN = 8

class SignUp : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding

    private var isKeyboardOpen = false

    private var wasEmailFocused = false
    private var wasPasswordFocused = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Перевірка, чи є збережений стан
        if (savedInstanceState != null) {
            // Відновлення даних із збереженого стану
            wasEmailFocused = savedInstanceState.getBoolean("wasEmailFocused", false)
            updateEmailErrorMessage()
            wasPasswordFocused = savedInstanceState.getBoolean("wasPasswordFocused", false)
            updatePasswordErrorMessage()
            updatePasswordTransformation()
        }

        if (procAutoLogin()) return
        procListeners()
        procPasswordTransformation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Збереження стану при знищенні активності
        outState.putBoolean("wasEmailFocused", wasEmailFocused)
        outState.putBoolean("wasPasswordFocused", wasPasswordFocused)
        super.onSaveInstanceState(outState)
    }

    private fun procListeners() {
        binding.textInputEditTextEmail.setOnFocusChangeListener { _, _ -> procEmailError() }
        binding.textInputEditTextPassword.setOnFocusChangeListener { _, _ -> procPasswordError() }

        //Використовуйте бібліотеки, такі як "RxKeyboard" або "KeyboardVisibilityEvent"
        window.decorView.rootView.viewTreeObserver.addOnGlobalLayoutListener {
            handleKeyboardState()
        }

        binding.buttonRegister.setOnClickListener {
            procClickRegisterButton()
        }
    }

    private fun procPasswordTransformation() {
        log("create TM")
        val customTransformationMethod = PasswordTransformation(getImgForPassword())

        log("set TM to EditText")
        binding.textInputEditTextPassword.transformationMethod = customTransformationMethod
        binding.textInputEditTextPassword.changeTextToDots()
    }

    private fun getImgForPassword(): MyImg {
        log("get img")
        val imageDrawable: Drawable? = ContextCompat.getDrawable(
            this,
            R.drawable.password_transformation_img
        )
        val passImgW = resources.getDimension(R.dimen.password_transform_img_width)
        val passImgH = resources.getDimension(R.dimen.password_transform_img_height)
        return MyImg(imageDrawable, passImgW, passImgH)
    }

    private fun updatePasswordTransformation() {
        binding.textInputEditTextPassword.setText("12345")
    }

    private fun procAutoLogin(): Boolean {
        val sharedPreferences = getSharedPreferences("AutoLogin", MODE_PRIVATE)
        val email = sharedPreferences.getString("Email", null)
        val password = sharedPreferences.getString("Password", null)

        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            val intent = Intent(this, MyProfile::class.java)
            startActivity(intent)
            finish()
            return true
        }

        return false
    }

    private fun procEmailError() {
        binding.textInputEditTextEmail.setText(
            binding.textInputEditTextEmail.text.toString().trim()
        )

        updateEmailErrorMessage()

        wasEmailFocused = true
    }

    private fun updateEmailErrorMessage() {
        if (wasEmailFocused) binding.textInputLayoutEmail.helperText =
            getEmailErrorMessage(binding.textInputEditTextEmail.text?.toString())
    }

    private fun procPasswordError() {
        updatePasswordErrorMessage()

        wasPasswordFocused = true
    }

    private fun updatePasswordErrorMessage() {
        if (wasPasswordFocused) binding.textInputLayoutPassword.helperText =
            getPasswordErrorMessage(binding.textInputEditTextPassword.text?.toString())
    }

    private fun getEmailErrorMessage(s: String?): String? {
        return if (s != null) {
            val hasSymbols = hasEmailAllSymbols(s)

            when {
                s.isEmpty() -> "The field is empty."
                s.length < MIN_EMAIL_LEN -> "E-mail must include a minimum of 8 characters."
                !hasSymbols[0] -> "E-mail must include dot."
                !hasSymbols[1] -> "E-mail must include '@' symbol."
                hasSymbols[2] -> "Email should not include spaces."
                else -> null
            }
        } else null
    }

    private fun getPasswordErrorMessage(s: String?): String? {
        return if (s != null) {
            val hasSymbols = hasPasswordAllSymbols(s)

            when {
                s.isEmpty() -> "The field is empty."
                s.length < MIN_PASSWORD_LEN -> "Your password must include a minimum of 8 characters."
                !hasSymbols[0] -> "Your password must include a digit."
                !hasSymbols[1] -> "Your password must include a letter."
                !hasSymbols[2] -> "Your password must include a upper case letter."
                !hasSymbols[3] -> "Your password must include a lower case letter."
                hasSymbols[4] -> "Your password should not include spaces."
                else -> null
            }
        } else null
    }

    private fun hasEmailAllSymbols(s: String): BooleanArray {
        var hasDot = false
        var hasEmailSymbol = false // @
        var hasSpace = false

        for (char in s) {
            if (char == '.') hasDot = true
            if (char == '@') hasEmailSymbol = true
            if (char == ' ') hasSpace = true
        }

        return booleanArrayOf(
            hasDot,
            hasEmailSymbol,
            hasSpace
        )
    }

    private fun hasPasswordAllSymbols(s: String): BooleanArray {
        var hasDigit = false
        var hasLetter = false
        var hasUpperCase = false
        var hasLowerCase = false
        var hasSpace = false

        for (char in s) {
            if (char.isDigit()) hasDigit = true
            if (char.isLetter()) hasLetter = true
            if (char.isUpperCase()) hasUpperCase = true
            if (char.isLowerCase()) hasLowerCase = true
            if (char == ' ') hasSpace = true
        }

        return booleanArrayOf(
            hasDigit,
            hasLetter,
            hasUpperCase,
            hasLowerCase,
            hasSpace
        )
    }

    private fun handleKeyboardState() {
        val rect = Rect()
        window.decorView.rootView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = window.decorView.rootView.height
        val keypadHeight = screenHeight - rect.bottom

        if (keypadHeight > screenHeight * 0.15) {
            if (!isKeyboardOpen) {
                isKeyboardOpen = true
                onKeyboardOpened()
            }
        } else {
            if (isKeyboardOpen) {
                isKeyboardOpen = false
                onKeyboardClosed()
            }
        }
    }

    /**
     * Дії при відкритті клавіатури
     */
    private fun onKeyboardOpened() {
    }

    /**
     * Дії при закритті клавіатури
     */
    private fun onKeyboardClosed() {
        binding.textInputEditTextEmail.clearFocus()
        binding.textInputEditTextPassword.clearFocus()
    }

    private fun procClickRegisterButton() {
        val intent = Intent(
            this,
            MyProfile::class.java
        )

        val emailErrorMessage =
            getEmailErrorMessage(binding.textInputEditTextEmail.text?.toString())
        val passwordErrorMessage =
            getPasswordErrorMessage(binding.textInputEditTextPassword.text?.toString())

        binding.textInputLayoutEmail.helperText = emailErrorMessage
        binding.textInputLayoutPassword.helperText = passwordErrorMessage

        if (emailErrorMessage == null && passwordErrorMessage == null) {
            intent.putExtra(
                "email",
                binding.textInputEditTextEmail.text?.toString()
            )
            intent.putExtra(
                "password",
                binding.textInputEditTextPassword.text?.toString()
            )

            if (binding.checkBox.isChecked) {
                val sharedPreferences = getSharedPreferences(
                    "AutoLogin",
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putString(
                    "Email",
                    binding.textInputEditTextEmail.text?.toString()
                )
                editor.putString(
                    "Password",
                    binding.textInputEditTextPassword.text?.toString()
                )
                editor.apply()
            }

            startActivity(intent)

            overridePendingTransition(
                R.anim.fade_in,
                R.anim.fade_out
            )
        }
    }
}