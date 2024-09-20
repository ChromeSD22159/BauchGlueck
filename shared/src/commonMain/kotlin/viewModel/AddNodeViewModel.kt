package viewModel

import data.Moods
import data.Repository
import data.model.Mood
import data.local.entitiy.Node
import org.koin.core.component.inject
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import util.DateRepository
import util.utcMillis

class AddNodeViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private var _allMoods = MutableStateFlow(Moods.list.toMutableList())
    val allMoods = _allMoods.asStateFlow()

    private var _currentMoods = MutableStateFlow(mutableListOf<Mood>())
    val currentMoods = _currentMoods.asStateFlow()

    private var _node = MutableStateFlow("")
    val node = _node.asStateFlow()

    private var _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    val textFieldDisplayLength : String
        get() = "${_node.value.count()}/${maxCharacters}"

    val userNodes = repository.nodeRepository.getAllNodes()

    private val maxCharacters = 512

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _allMoods.emit(Moods.list.toMutableList())
        }
    }

    private fun showMessage(error: String) {
        viewModelScope.launch {
            if (error.count() <= maxCharacters) {
                _message.value = error

                delay(5000)

                _message.value = ""
            }
        }
    }

    fun saveNode(finished: () -> Unit = {}) {
        viewModelScope.launch {
            repository.nodeRepository.insert(
                Node(
                    text = node.value,
                    moodsRawValue = Json.encodeToString(currentMoods.value),
                    date = Clock.System.utcMillis
                )
            )

            showMessage("Gespeichert")

            delay(1000)

            finished()
        }
    }

    fun updateNodeText(text: String) {
        if (text.count() <= maxCharacters) {
            _node.value = text
        }
    }

    fun currentMoodListContainsMood(moodIndex: Int): Boolean {
        return _currentMoods.value.contains(allMoods.value[moodIndex])
    }

    fun onClickOnMood(index: Int) {
        if (currentMoodListContainsMood(index)) {
            removeMood(allMoods.value[index])
            updateMoodFromDataList(index, false)
        } else {
            addMood(allMoods.value[index])
            updateMoodFromDataList(index, true)
        }
    }

    private fun addMood(mood: Mood) {
        _currentMoods.value = _currentMoods.value.toMutableList().apply {
            add(mood)
        }
    }

    private fun removeMood(mood: Mood) {
        _currentMoods.value = _currentMoods.value.toMutableList().apply {
            remove(mood)
        }
    }

    private fun updateMoodFromDataList(moodIndex: Int, value: Boolean) {
        _allMoods.value[moodIndex].isOnList = value
    }
}