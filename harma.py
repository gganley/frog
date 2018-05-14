import scipy.signal as signal
from scipy.io.wavfile import read as wavread
from scipy.ndimage.filters import gaussian_filter
import numpy as np


def harma(audio_file):
    sample_rate, audio_data = wavread(audio_file)

    f_domain, t_domain, sxx = signal.spectrogram(audio_data, sample_rate, 'hamming', 512, int(512 * (1 - 0.25)))

    sxx = step_2(sxx)
    sxx, a, t, output_syllables = syllable(sxx, [], [], 0, [])

    # The following is a complete hack
    a = a[:-1]
    return sxx, a, t


def syllable(sxx, a, t, n, output_syllables):
    beta = pow(10, 18 / 20)
    f_n, t_n = np.unravel_index(np.argmax(sxx, axis=None), sxx.shape)

    a.insert(n, {0: decibel(sxx[(f_n, t_n)])})
    if a[n][0] < a[0][0] - beta:
        return sxx, a, t

    limit = a[n][0] - beta
    backward_trace = trace_syllable(sxx[..., t_n-1::-1], limit)
    backward = dict(zip(range(-1, -len(backward_trace) - 1, -1), backward_trace))

    forward_trace = trace_syllable(sxx[..., t_n+1:], limit)
    forward = dict(zip(range(1, len(forward_trace) + 1), forward_trace))

    t_s = t_n - len(backward_trace)
    t_e = len(forward_trace) + t_n

    t.insert(n, (t_s, t_n, t_e))
    output_syllables.insert(n, sxx[..., t_s:t_e+1])
    sxx[..., t_s:t_e+1] = np.zeros((sxx.shape[0], t_e - t_s + 1))

    return syllable(sxx, a, t, n + 1, output_syllables)


def step_3(sxx):
    max_idx = np.unravel_index(np.argmax(sxx, axis=None), sxx.shape)
    return max_idx


def step_2(sxx):
    sxx = gaussian_filter(sxx, 10)  # I think that `sigma` may be wrong, but I have no way to know
    return sxx


def decibel(intensity):
    return 20*np.log10(intensity)


def trace_syllable(sxx, limit):
    output = []
    for frame in sxx.T:
        max_val = max(frame)
        if decibel(max_val) > limit:
            output.append(decibel(max_val))
        else:
            return output
