package com.wadud.gads.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wadud.gads.MainActivity
import com.wadud.gads.databinding.ConfirmationDialogBinding
import com.wadud.gads.databinding.FailureDialogBinding
import com.wadud.gads.databinding.SubmissionFragmentBinding
import com.wadud.gads.databinding.SuccessDialogBinding
import com.wadud.gads.network.LoadingStatus
import com.wadud.gads.ui.viewModels.SubmissionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.submission_fragment.*


@AndroidEntryPoint
class SubmissionFragment : Fragment() {


    val mainActivity: MainActivity
        get() {
            return activity as? MainActivity ?: throw IllegalStateException("Not attached!")
        }

    private val viewModel: SubmissionViewModel by viewModels()
    private lateinit var binding: SubmissionFragmentBinding
    private lateinit var confirmationDialogBinding: ConfirmationDialogBinding
    private lateinit var successDialogBinding: SuccessDialogBinding
    private lateinit var failureDialogBinding: FailureDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SubmissionFragmentBinding.inflate(inflater, container, false)
        confirmationDialogBinding = ConfirmationDialogBinding.inflate(inflater, container, false)
        successDialogBinding = SuccessDialogBinding.inflate(inflater, container, false)
        failureDialogBinding = FailureDialogBinding.inflate(inflater, container, false)
        binding.submitButton.setOnClickListener {
            val dialog =
                MaterialAlertDialogBuilder(requireContext()).setView(confirmationDialogBinding.root)
                    .show()
            confirmationDialogBinding.imageView.setOnClickListener {
                dismissDialog(dialog)
            }
            confirmationDialogBinding.button.setOnClickListener {
                makeSubmission()
                dismissDialog(dialog)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            when (it) {
                is LoadingStatus.Loading -> mainActivity.showLoading(it.message)
                is LoadingStatus.Success -> mainActivity.dismissLoading()
                is LoadingStatus.Error -> mainActivity.dismissLoading()
            }
        }
        viewModel.success.observe(viewLifecycleOwner) {
            when (it) {
                true -> showDialog(successDialogBinding.root)
                false -> showDialog(failureDialogBinding.root)

            }
        }
        return binding.root
    }

    private fun showDialog(view: View) {
        MaterialAlertDialogBuilder(requireContext()).setView(view)
            .show()
    }

    private fun dismissDialog(dialog: AlertDialog) {
        dialog.dismiss()
    }

    private fun makeSubmission() {
        if (getValidInput()) {
            viewModel.makeSubmission(
                binding.emailEditText.text.toString(),
                binding.firstNameEditText.text.toString(),
                binding.lastNameEditText.text.toString(),
                binding.gitHubEditText.text.toString()
            )
        }
    }

    private fun getValidInput(): Boolean {
        if (binding.emailEditText.text.isNullOrEmpty() || binding.gitHubEditText.text.isNullOrEmpty() || binding.lastNameEditText.text.isNullOrEmpty()) {
            binding.LastNameTextInputLayout.error = "All Fields Are required"
            binding.emailTextInputLayout.error = "Input a valid Email Address"
            return false
        }
        binding.LastNameTextInputLayout.error = null
        binding.emailTextInputLayout.error = null

        return true
    }


}