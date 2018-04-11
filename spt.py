from scipy import signal
from scipy.ndimage import gaussian_filter

def spt(vocalization, sampling_rate):
    # Returns a SPT
    # Each part of a SPT onsists of...
    # A starting time /t_s/
    # A stop time /t_e/
    # And frequency bin index of each of the peaks within the track /f_t/
    f, t, sxx = signal.spectrogram(vocalization, sampling_rate, 'hamming', 128, int(128 - (0.85 * 128)))

