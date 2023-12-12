package com.example.rbhtest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rbhtest.adapters.TaskListAdapter
import com.example.rbhtest.databinding.FragmentFirstBinding
import com.example.rbhtest.utils.ApiStatus
import com.example.rbhtest.utils.SwipeToDeleteCallback
import com.example.rbhtest.viewModels.AppViewModel
import java.net.HttpURLConnection
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

  private var _binding: FragmentFirstBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!


  private val viewModel: AppViewModel by activityViewModels()
  private val adapter: TaskListAdapter by lazy { TaskListAdapter() }

  private val LIMIT = 20
  private val currentOffset = 0
  private val currentTab = "TODO"

  private var pauseTime: Long = 0

  private val dotsList: List<CardView> by lazy {
    listOf(
      binding.pin.cvDot0,
      binding.pin.cvDot1,
      binding.pin.cvDot2,
      binding.pin.cvDot3,
      binding.pin.cvDot4,
      binding.pin.cvDot5
    )
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    _binding = FragmentFirstBinding.inflate(inflater, container, false)

    activity?.let {
      it.window.statusBarColor = resources.getColor(R.color.colorPrimary, it.theme)
//      it.window.navigationBarColor = resources.getColor(R.color.colorPrimary, it.theme)
    }

    requestTask("TODO", LIMIT, currentOffset)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

//    binding.buttonFirst.setOnClickListener {
//      findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//    }

    binding.rvTask.adapter = adapter
    binding.rvTask.layoutManager = LinearLayoutManager(requireContext())

    val item = object : SwipeToDeleteCallback(requireContext(), 0, ItemTouchHelper.LEFT) {
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        viewModel.removeTask(viewHolder.adapterPosition)
      }
    }
    ItemTouchHelper(item).attachToRecyclerView(binding.rvTask)

    setupViewListener()
    setupPinScreenListener()
    setupViewModelObserver()
  }

  override fun onPause() {
    super.onPause()

    pauseTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
  }

  override fun onResume() {
    super.onResume()

    if (shouldShowPinScreen(pauseTime)) {
      binding.pin.root.visibility = View.VISIBLE
    }
    pauseTime = 0L
  }

  private fun shouldShowPinScreen(startTime: Long): Boolean {
    val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    val diff = now - startTime
    return diff > 10 && startTime != 0L
  }

  private fun requestTask(status: String, limit: Int = LIMIT, offset: Int = 0) {
    viewModel.requestGetTask(status, limit, offset)
  }

  private fun setupViewListener() {
    binding.fab.setOnClickListener {
      // TODO
      // add
    }
    binding.cvTodoTab.setOnClickListener {
      it as CardView
      requestTask("TODO", LIMIT, currentOffset)
      it.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.active))
      binding.cvDoingTab.setCardBackgroundColor(
        ContextCompat.getColor(
          requireContext(),
          R.color.inactive
        )
      )
      binding.cvDoneTab.setCardBackgroundColor(
        ContextCompat.getColor(
          requireContext(),
          R.color.inactive
        )
      )
    }
    binding.cvDoingTab.setOnClickListener {
      it as CardView
      requestTask("DOING", LIMIT, currentOffset)
      it.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.active))
      binding.cvTodoTab.setCardBackgroundColor(
        ContextCompat.getColor(
          requireContext(),
          R.color.inactive
        )
      )
      binding.cvDoneTab.setCardBackgroundColor(
        ContextCompat.getColor(
          requireContext(),
          R.color.inactive
        )
      )
    }
    binding.cvDoneTab.setOnClickListener {
      it as CardView
      requestTask("DONE", LIMIT, currentOffset)
      it.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.active))
      binding.cvTodoTab.setCardBackgroundColor(
        ContextCompat.getColor(
          requireContext(),
          R.color.inactive
        )
      )
      binding.cvDoingTab.setCardBackgroundColor(
        ContextCompat.getColor(
          requireContext(),
          R.color.inactive
        )
      )
    }
  }

  private fun setupPinScreenListener() {
    val pinScreen = binding.pin
    pinScreen.cvKeypad0.setOnClickListener { onPinClicked('0') }
    pinScreen.cvKeypad1.setOnClickListener { onPinClicked('1') }
    pinScreen.cvKeypad2.setOnClickListener { onPinClicked('2') }
    pinScreen.cvKeypad3.setOnClickListener { onPinClicked('3') }
    pinScreen.cvKeypad4.setOnClickListener { onPinClicked('4') }
    pinScreen.cvKeypad5.setOnClickListener { onPinClicked('5') }
    pinScreen.cvKeypad6.setOnClickListener { onPinClicked('6') }
    pinScreen.cvKeypad7.setOnClickListener { onPinClicked('7') }
    pinScreen.cvKeypad8.setOnClickListener { onPinClicked('8') }
    pinScreen.cvKeypad9.setOnClickListener { onPinClicked('9') }
    pinScreen.cvKeypadDel.setOnClickListener { viewModel.removeLastInputPin() }
    viewModel.inputPin.observe(viewLifecycleOwner) { value ->
      val len = value.length
      handleRenderDot(len)
      if (viewModel.isInputAtMax()) {
        if (viewModel.isInputCorrect()) {
          handleCorrectPin()
        } else {
          handleIncorrectPin()
        }
      }
    }
  }

  private fun handleCorrectPin() {
    viewModel.clearInputPin()
    binding.pin.tvPinStatus.text = getString(R.string.pinDesc)
    binding.pin.root.visibility = View.GONE
  }

  private fun handleIncorrectPin() {
    viewModel.clearInputPin()
    binding.pin.tvPinStatus.text = getString(R.string.pinIncorrectPin)
    // TODO
    // anim
  }

  private fun handleRenderDot(inputLen: Int) {
    repeat(dotsList.size) {
      if (inputLen > it || viewModel.isInputAtMax()) {
        dotsList[it].setCardBackgroundColor(
          ContextCompat.getColor(
            requireContext(),
            R.color.dotFilled
          )
        )
      } else {
        dotsList[it].setCardBackgroundColor(
          ContextCompat.getColor(
            requireContext(),
            R.color.dotNotFilled
          )
        )
      }
    }
  }

  private fun onPinClicked(char: Char): Boolean {
    if (viewModel.isInputAtMax()) return false
    viewModel.addInputChar(char)
    return true
  }


  private fun setupViewModelObserver() {
    viewModel.todoList.observe(viewLifecycleOwner) { list ->
      list?.let { adapter.submitList(it) }
      Log.e("TEST", "hello ${list.size.toString()}")
    }

    viewModel.todoListResponse.observe(viewLifecycleOwner) { response ->
      when (response.status) {
        ApiStatus.SUCCESS -> {
          viewModel.resetTodoListResponse()
        }
//        ApiStatus.LOADING -> showProgressBar()
        ApiStatus.FAIL -> {
          if (response.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
//            val snackText: String = response.statusCode.let {
//              if (it == 404) {
//                "ไม่พบข้อมูลในระบบ กรุณาตรวจสอบอีกครั้ง"
//              } else "เกิดข้อผิดพลาดขณะเชื่อมต่อเซิฟเวอร์ Code:$it"
//            }
//            makeSnackLong(snackText, 3000)
//            hideProgressBar()
          }
          viewModel.resetTodoListResponse()
        }

        ApiStatus.EXCEPTION -> {
//          makeSnackLong("เกิดข้อผิดพลาด Error:${response.message}")
//          hideProgressBar()
          viewModel.resetTodoListResponse()
        }
      }
    }
  }


  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}