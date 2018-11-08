import numpy as np
from scipy import signal
import scipy.io.wavfile as wav
from scipy.ndimage import gaussian_filter

def decibel(intensity):
    if intensity > 0:
        return 20 * np.log10(intensity)
    else:
        return 0


def pos_of_maxarg(input_array):
    return np.unravel_index(np.argmax(input_array), input_array.shape)


def trace_syllable(sxx, min_db):
    sxx_slice = sxx[..., 0]
    idx = pos_of_maxarg(sxx_slice)[0]
    amplitude = decibel(sxx_slice[idx])
    if amplitude < min_db:
        return []
    else:
        return [(idx, amplitude)] + trace_syllable(sxx[..., 1:], min_db)


def index_o_a(sxx, t_0, min_db):
    # f_t == forward trace
    f_t_omegas, f_t_alphas = zip(*trace_syllable(sxx[..., t_0 + 1:], min_db))

    # Keep in mind this is reverse, it will have to indexed as such
    # r_t == reverse trace
    r_t_omegas, r_t_alphas = zip(*trace_syllable(sxx[..., t_0 - 1::-1], min_db))

    t_s = t_0 - len(r_t_omegas)
    t_e = t_0 + len(f_t_omegas)

    # So the funny buissesness with the giving and taking away of /t_0/ is strictly to keep consistant with
    # Harmas paper, in the furture I may refactor it to make more sense from a CS standpoint

    tau = list(range(-(t_0 - t_s), 0)) + list(range(1, t_e - t_0 + 1))

    omegas = r_t_omegas[::-1] + f_t_omegas
    alphas = r_t_alphas[::-1] + f_t_alphas
    ret_omega = zip(tau, omegas)
    ret_alpha = zip(tau, alphas)

    return t_s, t_e, ret_omega, ret_alpha


def syllable(sxx):
    f_0, t_0 = pos_of_maxarg(sxx)
    omega_0 = f_0
    alpha_0 = decibel(sxx[(f_0, t_0)])
    min_db = alpha_0 - 30

    # Trace syllable on either side of /t_0/
    # TODO: test to make sure that w[0] and a[0] are the max

    omega_n = [(0, omega_0)]
    alpha_n = [(0, alpha_0)]

    t_s, t_e, ext_omega, ext_alpha = index_o_a(sxx, t_0, min_db)

    omega_n.extend(ext_omega)
    alpha_n.extend(ext_alpha)

    sxx[..., t_s:t_e + 1] = np.zeros(sxx[..., t_s:t_e + 1].shape)

    return omega_n, alpha_n, sxx


def harma(spectrogram_object, nsyllables):
    f, t, sxx = spectrogram_object
    sxx = gaussian_filter(sxx, 5)
    omega = []
    alpha = []
    for i in range(nsyllables):
        w, a, sxx = syllable(sxx)
        omega.append(w)
        alpha.append(a)
    return omega, alpha, sxx
