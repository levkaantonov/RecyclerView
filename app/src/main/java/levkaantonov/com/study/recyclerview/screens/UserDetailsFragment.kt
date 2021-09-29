package levkaantonov.com.study.recyclerview.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import levkaantonov.com.study.recyclerview.R
import levkaantonov.com.study.recyclerview.databinding.FragmentUserDetailsBinding
import levkaantonov.com.study.recyclerview.tasks.SuccessResult

class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserDetailsViewModel by viewModels { factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadUser(requireArguments().getLong(ARG_USER_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.actionGoBack.observe(viewLifecycleOwner) {
            it.getValue()?.let { navigator().goBack() }
        }
        viewModel.actionShowToast.observe(viewLifecycleOwner) {
            it.getValue()?.let { messageRes -> navigator().toast(messageRes) }
        }

        viewModel.state.observe(viewLifecycleOwner) {
            binding.contentContainer.visibility = if (it.showContent) {
                val userDetails = (it.userDetailsResult as SuccessResult).data
                binding.tvUserName.text = userDetails.user.name
                binding.tvUerDetails.text = userDetails.details
                if (userDetails.user.photo.isNotBlank()) {
                    Glide.with(this@UserDetailsFragment)
                        .load(userDetails.user.photo)
                        .circleCrop()
                        .into(binding.ivPhoto)
                } else {
                    Glide.with(this@UserDetailsFragment)
                        .load(R.drawable.ic_user_avatar)
                        .into(binding.ivPhoto)
                }
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.progressBar.visibility = if (it.showProgress) View.VISIBLE else View.GONE
            binding.btnDelete.isEnabled = it.enableDeleteButton
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteUser()
        }
    }

    companion object {
        private const val ARG_USER_ID = "ARG_USER_ID"

        fun newInstance(id: Long): UserDetailsFragment {
            val fragment = UserDetailsFragment()
            fragment.arguments = bundleOf(ARG_USER_ID to id)
            return fragment
        }
    }
}