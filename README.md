# dn-audio-ext
> For JPHP and DevelNext ([http://develnext.org](http://develnext.org)).

Пакет расширений для работы с аудио

### Возможности
- Запись звука с микрофона
	- Класс `VoiceRecorder`

### Пример

#### VoiceRecorder
```php 
use php\audio\VoiceRecorder; 

$recorder = new VoiceRecorder(); 

// ограничение времени записи(в мс)
$recorder->maxRecordTime = 5000; 

// -1 для неограниченной записи
$recorder->maxRecordTime = -1; 

// запуск записи
$recorder->start('voice_file.wav'); 

// прерывание записи
$recorder->stop(); 
```

### Сборка

1. Откройте консоль(bash, cmd)
2. Переместитесь в корень пакета `dn-audio-bundle`(cd path/to/dn-audio-bundle)
3. Используйте команду сборщика Gradle

```bash 
// Windows 
gradlew bundle 
// Linux 
chmodx +x gradlew 
./gradlew bundle 
``` 

4. В папке `dn-audio-bundle/build` вы найдете бандл с расширением `.bundle`
