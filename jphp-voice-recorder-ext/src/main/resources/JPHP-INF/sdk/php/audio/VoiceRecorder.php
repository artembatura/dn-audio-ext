<?php
namespace php\audio;


/**
 * Class VoiceRecorder
 * @package php\audio
 */
class VoiceRecorder
{
    /**
     * Stop Recording on reach maxRecordTime(ms)
     * @var int
     */
    public $maxRecordTime = 5000;

    /**
     * VoiceRecorder constructor
     */
    public function __construct()
    {
    }
    
    /**
     * Start voice recording
     * @param string $pathToFile
     */
    public function start($pathToFile)
    {
    }
    
    /**
     * Stop voice recording
     */
    public function stop()
    {
    }
}