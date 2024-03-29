import numpy as np
from scipy import signal
from syllable import decibel


def spt(syllable_object):
    # Returns a SPT
    # Each part of a SPT onsists of...
    # A starting time /t_s/
    # A stop time /t_e/
    # And frequency bin index of each of the peaks within the track /f_t/

    # Step 1
    sampling_rate, syllable = syllable_object
    f, t, sxx = signal.spectrogram(syllable, sampling_rate, 'hamming', 128, int(128 - (0.85 * 128)))

    # Step 2: For each frame, the maximum intensity is selected with a minimum required value of 3 dB. This can result in
    # not all time frames containing peaks.    denotes a peak with   representing the peak index (not the time frame it is
    # contained in). The 3 dB threshold is chosen empirically.

    p = []
    for frame in sxx.T:
        idx = np.argmax(frame)
        if decibel(frame[idx]) > 3:
            p.append(idx)


def remove_quiet_track(sxx):
    return sxx