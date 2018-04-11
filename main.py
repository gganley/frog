from scipy import signal
import scipy.io.wavfile as wav
from syllable import harma


if __name__ == '__main__':
    sample_rate, data_array = wav.read('/Users/gganley/Processed Frog/output_bf5.wav')
    spectrogram_object = signal.spectrogram(data_array,
                                            sample_rate,
                                            'hamming',
                                            nperseg=512,
                                            noverlap=int(512 - (512 * 0.25)))
    omega, alpha, sxx = harma(spectrogram_object, 3)
